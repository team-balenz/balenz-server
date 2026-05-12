package com.example.Balenz.keyword.service;

import com.example.Balenz.article.dto.SimpleArticleDto;
import com.example.Balenz.article.entity.Article;
import com.example.Balenz.article.entity.FrameType;
import com.example.Balenz.article.repository.ArticleRepository;
import com.example.Balenz.keyword.dto.HotIssueDataDto;
import com.example.Balenz.keyword.entity.Keyword;
import com.example.Balenz.keyword.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class HotKeywordService {

    private final KeywordRepository keywordRepository;
    private final KeywordService keywordService;
    private final ArticleRepository articleRepository;

    /** 이념 관점 페이지에서 hot-issue 섹션 데이터 조회 */
    public HotIssueDataDto getHotIssueData() {
        // 1. 오늘 조회수가 가장 높은 키워드 조회
        LocalDate serviceDate = keywordService.getCurrentServiceDate();
        List<Keyword> keywords = keywordRepository.findTop6ByServiceDateOrderByViewCountDescIdDesc(serviceDate);

        if (keywords.isEmpty()) {
            return HotIssueDataDto.builder()
                    .keywordAndArticles(List.of()).build();
        }

        List<HotIssueDataDto.KeywordAndArticleDto> keywordAndArticles = new ArrayList<>();

        // 2. 각 키워드에서 인기있는 기사 2개 조회
        for (Keyword keyword : keywords) {
            List<Article> strongValue = articleRepository.findTop2ByKeyword_IdAndFrameTypeOrderByViewCountDesc(keyword.getId(), FrameType.STRONG_VALUE.name());
            List<Article> value = articleRepository.findTop2ByKeyword_IdAndFrameTypeOrderByViewCountDesc(keyword.getId(), FrameType.VALUE.name());
            List<Article> neutral = articleRepository.findTop2ByKeyword_IdAndFrameTypeOrderByViewCountDesc(keyword.getId(), FrameType.NEUTRAL.name());
            List<Article> norm = articleRepository.findTop2ByKeyword_IdAndFrameTypeOrderByViewCountDesc(keyword.getId(), FrameType.NORM.name());
            List<Article> strongNorm = articleRepository.findTop2ByKeyword_IdAndFrameTypeOrderByViewCountDesc(keyword.getId(), FrameType.STRONG_NORM.name());

            // 각 프레임타입별로 기사 2개씩 조회 후 그 중 랜덤으로 2개 반환
            List<Article> valueCandidates = Stream.concat(strongValue.stream(), value.stream()).toList();
            List<Article> normCandidates = Stream.concat(strongNorm.stream(), norm.stream()).toList();

            // 3. value에서 1개, norm에서 1개 선택
            List<Article> selectedArticles = new ArrayList<>();

            pickOne(valueCandidates).ifPresent(selectedArticles::add);
            pickOne(normCandidates).ifPresent(selectedArticles::add);

            // 4. 선택된 기사 개수 합이 2가 아닌 경우 남은 건 neutral에서
            fillRemaining(selectedArticles, neutral);

            // 5. 그래도 2개가 안되는 경우 value나 neutral에서 추가
            fillRemaining(selectedArticles, valueCandidates);
            fillRemaining(selectedArticles, normCandidates);

            List<SimpleArticleDto> articles = selectedArticles.stream()
                    .map(this::toSimpleArticleDto).toList();

            HotIssueDataDto.KeywordAndArticleDto keywordAndArticle = HotIssueDataDto.KeywordAndArticleDto.builder()
                    .id(keyword.getId())
                    .name(keyword.getName())
                    .articles(articles).build();

            keywordAndArticles.add(keywordAndArticle);
        }

        return HotIssueDataDto.builder()
                .keywordAndArticles(keywordAndArticles).build();
    }

    private SimpleArticleDto toSimpleArticleDto(Article article) {
        return SimpleArticleDto.builder()
                .id(article.getId())
                .title(article.getTitle())
                .newsAgencyName(article.getNewsAgency().getName())
                .frameType(article.getFrameType())
                .imageUrl(article.getImageUrl()).build();
    }

    private Optional<Article> pickOne(List<Article> articles) {
        return pick(articles, 1).stream().findFirst();
    }

    private List<Article> pick(List<Article> articles, int count) {
        ArrayList<Article> copy = new ArrayList<>(articles);
        Collections.shuffle(copy);
        return copy.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    /** 선택된 기사 개수가 2 미만일 경우 채우기 */
    private void fillRemaining(List<Article> selected, List<Article> source) {
        if (selected.size() >= 2) return;

        List<Long> selectedIds = selected.stream()
                .map(Article::getId).toList();

        List<Article> candidates = source.stream()
                .filter(a -> !selectedIds.contains(a.getId())) // 중복 방지
                .toList();

        selected.addAll(pick(candidates, 2 - selectedIds.size()));
    }

}
