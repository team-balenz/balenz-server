package com.example.Balenz.article.service;

import com.example.Balenz.article.dto.ArticleDetailDto;
import com.example.Balenz.article.dto.RelatedArticleDto;
import com.example.Balenz.article.entity.Article;
import com.example.Balenz.article.entity.FrameType;
import com.example.Balenz.article.repository.ArticleRepository;
import com.example.Balenz.global.exception.BaseException;
import com.example.Balenz.global.exception.ErrorCode;
import com.example.Balenz.keyword.dto.ScopeSectionResponseDto;
import com.example.Balenz.keyword.entity.Keyword;
import com.example.Balenz.keyword.repository.KeywordRepository;
import com.example.Balenz.keyword.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final KeywordRepository keywordRepository;
    private final KeywordService keywordService;

    public ArticleDetailDto getArticleDetail(Long id) {
        Article article = articleRepository.findById(id).orElseThrow(
                () -> new BaseException(ErrorCode.ARTICLE_NOT_FOUND, "해당 id의 기사를 찾을 수 없습니다."));

        // 연관기사 조회 -> DTO 생성
        Keyword keyword = article.getKeyword();
        // 1차 - 각 FrameType별로 4개씩 조회
        List<Article> strongValue = articleRepository.findTop4ByKeyword_IdAndFrameTypeAndIdNotOrderByPublishedAtDesc(keyword.getId(), FrameType.STRONG_VALUE, id);
        List<Article> value = articleRepository.findTop4ByKeyword_IdAndFrameTypeAndIdNotOrderByPublishedAtDesc(keyword.getId(), FrameType.VALUE, id);
        List<Article> neutral = articleRepository.findTop4ByKeyword_IdAndFrameTypeAndIdNotOrderByPublishedAtDesc(keyword.getId(), FrameType.NEUTRAL, id);
        List<Article> norm = articleRepository.findTop4ByKeyword_IdAndFrameTypeAndIdNotOrderByPublishedAtDesc(keyword.getId(), FrameType.NORM, id);
        List<Article> strongNorm = articleRepository.findTop4ByKeyword_IdAndFrameTypeAndIdNotOrderByPublishedAtDesc(keyword.getId(), FrameType.STRONG_NORM, id);

        // VALUE
        List<RelatedArticleDto> valueRelatedArticles = Stream
                .concat(value.stream(), strongValue.stream())
                .map(this::toRelatedArticleDto)
                .collect(Collectors.toList());

        // NORM
        List<RelatedArticleDto> normRelatedArticles = Stream
                .concat(norm.stream(), strongNorm.stream())
                .map(this::toRelatedArticleDto)
                .collect(Collectors.toList());

        // NEUTRAL
        List<RelatedArticleDto> neutralRelatedArticles = neutral.stream()
                .map(this::toRelatedArticleDto).collect(Collectors.toList());

        // 전체 - Value 3 Neutral 4 Norm 3 비율에 맞춰서 생성
        List<RelatedArticleDto> allRelatedArticles = Stream.of(
                pick(valueRelatedArticles, 3).stream(),
                pick(neutralRelatedArticles, 4).stream(),
                pick(normRelatedArticles, 3).stream()
                ).flatMap(s -> s)
                .collect(Collectors.toList());

        if (allRelatedArticles.size() < 10) {
            int remaining = allRelatedArticles.size() - 10;

            // 전체 후보 풀 만들기
            List<RelatedArticleDto> pool = Stream.of(
                            valueRelatedArticles,
                            neutralRelatedArticles,
                            normRelatedArticles).flatMap(List::stream)
                    .distinct()
                    .collect(Collectors.toList());

            // 이미 allRelatedArticles에 들어가있는 것 제외
            pool.removeAll(allRelatedArticles);

            // 부족한 만큼 추가
            allRelatedArticles.addAll(pick(pool, remaining));
        }

        LocalDate serviceDate = keywordService.getCurrentServiceDate();
        List<Keyword> keywords = keywordRepository.findTop6ByServiceDateOrderByViewCountDescIdDesc(serviceDate);
        List<ScopeSectionResponseDto.KeywordDto> hotKeywords;
        if (keywords.isEmpty()) {
            hotKeywords = List.of();
        } else {
            hotKeywords = keywordService.getKeywordDtos(keywords);
        }

        return ArticleDetailDto.builder()
                .title(article.getTitle())
                .newsAgencyName(article.getNewsAgency().getName())
                .publishedAt(article.getPublishedAt())
                .frameType(article.getFrameType())
                .summary(article.getSummary())
                .articleUrl(article.getArticleUrl())
                .valueRelatedArticles(shuffle(valueRelatedArticles))
                .normRelatedArticles(shuffle(normRelatedArticles))
                .neutralRelatedArticles(shuffle(neutralRelatedArticles))
                .allRelatedArticles(allRelatedArticles)
                .hotKeywords(hotKeywords).build();
    }

    private RelatedArticleDto toRelatedArticleDto(Article article) {
        return RelatedArticleDto.builder()
                .id(article.getId())
                .title(article.getTitle())
                .newsAgencyName(article.getNewsAgency().getName())
                .publishedAt(article.getPublishedAt())
                .frameType(article.getFrameType())
                .summary(article.getSummary()).build();
    }

    private List<RelatedArticleDto> shuffle(List<RelatedArticleDto> list) {
        Collections.shuffle(list);
        return list;
    }

    /** 리스트 shuffle 후 count 개수만큼 뽑기 */
    private List<RelatedArticleDto> pick(List<RelatedArticleDto> list, int count) {
        List<RelatedArticleDto> shuffled = shuffle(list);

        return shuffled.stream()
                .limit(count).collect(Collectors.toList());
    }

}