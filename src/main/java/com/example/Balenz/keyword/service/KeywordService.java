package com.example.Balenz.keyword.service;

import com.example.Balenz.article.entity.Article;
import com.example.Balenz.article.entity.FrameType;
import com.example.Balenz.article.repository.ArticleRepository;
import com.example.Balenz.keyword.dto.KeywordDto;
import com.example.Balenz.keyword.dto.ScopeSectionResponseDto;
import com.example.Balenz.keyword.entity.Category;
import com.example.Balenz.keyword.entity.DominantFrameType;
import com.example.Balenz.keyword.entity.Keyword;
import com.example.Balenz.keyword.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;
    private final ArticleRepository articleRepository;

    public ScopeSectionResponseDto getScopeSectionData(Category category) {
        // TODO : 업데이트 시간 확정 후 getCurrentServiceDate 기준 시간 변경
        // 1. 카테고리에 해당하는 오늘의 키워드들 조회
        LocalDate serviceDate = getCurrentServiceDate();

        List<Keyword> keywords;
        if (category == null) { // 전체 조회
            keywords = keywordRepository.findTop7ByServiceDateOrderByViewCountDescIdDesc(serviceDate);
        } else {
            keywords = keywordRepository.findTop7ByCategoryAndServiceDateOrderByViewCountDescIdDesc(category, serviceDate);
        }

        // 2. DTO 생성
        // 2-1. MainKeywordDto
        if (keywords.isEmpty()) {
            return ScopeSectionResponseDto.builder()
                    .mainKeyword(null)
                    .keywords(List.of()).build();
        }
        Keyword main = keywords.get(0);

        Optional<Article> valueArticle = articleRepository.findTopByKeyword_IdAndFrameTypeOrderByViewCountDesc(main.getId(), FrameType.VALUE.name());
        Optional<Article> normArticle = articleRepository.findTopByKeyword_IdAndFrameTypeOrderByViewCountDesc(main.getId(), FrameType.NORM.name());
        ScopeSectionResponseDto.MainKeywordDto mainKeywordDto = ScopeSectionResponseDto.MainKeywordDto.builder()
                .id(main.getId())
                .name(main.getName())
                .articleCount(getArticleCount(main.getId()))
                .valueArticleTitle(valueArticle.isPresent() ? valueArticle.get().getTitle() : null)
                .valueImageUrl(valueArticle.isPresent() ? valueArticle.get().getImageUrl() : null)
                .normArticleTitle(normArticle.isPresent() ? normArticle.get().getTitle() : null)
                .normImageUrl(normArticle.isPresent() ? normArticle.get().getImageUrl() : null).build();

        // 2-2. KeywordDto 리스트
        List<Keyword> subKeywords = keywords.stream().skip(1).toList();
        List<KeywordDto> keywordDtos = getKeywordDtos(subKeywords);


        return ScopeSectionResponseDto.builder()
                .mainKeyword(mainKeywordDto)
                .keywords(keywordDtos).build();
    }

    /** Keyword 리스트 -> KeywordDto 리스트 */
    public List<KeywordDto> getKeywordDtos(List<Keyword> keywords) {
        return keywords.stream()
                .map(k -> {
                    ScopeSectionResponseDto.ArticleCountDto countDto = getArticleCount(k.getId());

                    return KeywordDto.builder()
                            .id(k.getId())
                            .name(k.getName())
                            .imageUrl(k.getThumbnailUrl())
                            .articleCount(countDto)
                            .dominantFrameType(getDominantArticleFrameType(countDto)).build();
                }).toList();
    }

    /** 키워드에 해당하는 프레임별 기사 수 */
    public ScopeSectionResponseDto.ArticleCountDto getArticleCount(Long keywordId) {
        long valueCount = articleRepository.countByKeywordIdAndFrameType(keywordId, FrameType.VALUE);
        long neutralCount = articleRepository.countByKeywordIdAndFrameType(keywordId, FrameType.NEUTRAL);
        long normCount = articleRepository.countByKeywordIdAndFrameType(keywordId, FrameType.NORM);
        long total = valueCount + neutralCount + normCount;

        return ScopeSectionResponseDto.ArticleCountDto.builder()
                .value(valueCount)
                .neutral(neutralCount)
                .norm(normCount)
                .valueRatio(getRatio(valueCount, total))
                .neutralRatio(getRatio(neutralCount, total))
                .normRatio(getRatio(normCount, total))
                .build();
    }

    private double getRatio(long count, long total) {
        if (total == 0) return 0;

        double ratio = (double) count / total;
        return Math.round(ratio * 100) / 100.0; // 소수점 둘째 자리까지 반올림
    }

    /** 키워드 관련 기사 중 어떤 프레임의 기사가 가장 많은지 반환 **/
    public DominantFrameType getDominantArticleFrameType(ScopeSectionResponseDto.ArticleCountDto countDto) {
        Long value = countDto.getValue();
        Long neutral = countDto.getNeutral();
        Long norm = countDto.getNorm();

        if (value == neutral && neutral == norm)
            return DominantFrameType.BALANCED;

        return Stream.of(
                Map.entry(DominantFrameType.VALUE, value),
                Map.entry(DominantFrameType.NEUTRAL, neutral),
                Map.entry(DominantFrameType.NORM, norm))
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(DominantFrameType.BALANCED);
    }

    /** 사용자 조회용 현재 시점 serviceDate 계산
     * 하루의 기준 : 8:00 ~ 다음날 7:59
     * -> 8시 이전 - 전날 반환, 8시 이후 - 오늘 반환 */
    public LocalDate getCurrentServiceDate() {
        LocalDateTime now = LocalDateTime.now();
        // 0사 ~ 8시이면 전날 데이터, 8시 이후면 오늘 데이터
        return now.getHour() < 8
                ? now.toLocalDate().minusDays(1)
                : now.toLocalDate();
    }

    /** 데이터 사전 저장용 serviceDate (공개 대상 날짜) 계산 (8시에 공개할 데이터 미리 저장) */
    public LocalDate getPreparedServiceDate() {
        return LocalDate.now();
    }


    public List<KeywordDto> getHotKeywordDtos() {
        LocalDate serviceDate = getCurrentServiceDate();
        List<Keyword> keywords = keywordRepository.findTop6ByServiceDateOrderByViewCountDescIdDesc(serviceDate);
        List<KeywordDto> hotKeywords;
        if (keywords.isEmpty()) {
            hotKeywords = List.of();
        } else {
            hotKeywords = getKeywordDtos(keywords);
        }
        return hotKeywords;
    }

    /** 편향 분포 계산 */
    public int getBias(DominantFrameType dominantFrameType, ScopeSectionResponseDto.ArticleCountDto articleCount) {
        double maxRatio = switch (dominantFrameType) {
            case VALUE -> articleCount.getValueRatio();
            case NEUTRAL -> articleCount.getNeutralRatio();
            case NORM -> articleCount.getNormRatio();
            case BALANCED -> 0;
        };
        return (int) Math.round(maxRatio * 100);
    }

}
