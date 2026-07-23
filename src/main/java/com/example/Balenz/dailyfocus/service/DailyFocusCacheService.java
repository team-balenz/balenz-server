package com.example.Balenz.dailyfocus.service;

import com.example.Balenz.article.entity.Article;
import com.example.Balenz.article.entity.FrameType;
import com.example.Balenz.article.repository.ArticleRepository;
import com.example.Balenz.dailyfocus.dto.DailyFocusArticleDto;
import com.example.Balenz.dailyfocus.dto.DailyFocusCacheDto;
import com.example.Balenz.keyword.entity.Category;
import com.example.Balenz.keyword.entity.Keyword;
import com.example.Balenz.keyword.repository.KeywordRepository;
import com.example.Balenz.keyword.service.KeywordService;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyFocusCacheService {

    private final KeywordRepository keywordRepository;
    private final ArticleRepository articleRepository;
    private final KeywordService keywordService;

    @Cacheable(
            cacheNames = "daily-focus",
            key = "#category.name() + ':' + #serviceDate",
            sync = true
    )
    public DailyFocusCacheDto getDailyFocusCache(Category category, LocalDate serviceDate) {
        // 카테고리별 가장 조회수가 높은 키워드 조회
        Keyword keyword = keywordRepository.findTopByCategoryAndServiceDateOrderByViewCountDescIdDesc(category, serviceDate).orElse(null);

        if (keyword == null) return null;

        return createDailyFocusCacheDto(keyword);
    }

    private DailyFocusArticleDto toDailyFocusArticleDto(Article article) {
        return new DailyFocusArticleDto(
                article.getId(),
                article.getTitle(),
                article.getNewsAgency().getName(),
                article.getFrameType(),
                article.getSummary()
        );
    }

    @CachePut(
            cacheNames = "daily-focus",
            key = "#category.name() + ':' + #serviceDate"
    )
    public DailyFocusCacheDto refreshDailyFocusCache(Keyword keyword, Category category, LocalDate serviceDate) {
        return createDailyFocusCacheDto(keyword);
    }

    private DailyFocusCacheDto createDailyFocusCacheDto(Keyword keyword) {
        Long keywordId = keyword.getId();

        // 1. 조회수 순으로 키워드 내 기사 전체 조회
        List<Article> articles = articleRepository.findByKeywordIdOrderByTotalViewCountDesc(keywordId);

        // 2. 프레임타입별 조회수가 가장 높은 기사 조회
        Map<FrameType, Article> articleByFrameType = articles.stream()
                .collect(Collectors.toMap(
                        Article::getFrameType,
                        Function.identity(),
                        (first, second) -> first
                ));

        // 3. 좌우별로 기사 1개씩 조회 - 양극단 우선
        Article valueArticle = articleByFrameType.containsKey(FrameType.STRONG_VALUE)
                ? articleByFrameType.get(FrameType.STRONG_VALUE)
                : articleByFrameType.get(FrameType.VALUE);
        Article normArticle = articleByFrameType.containsKey(FrameType.STRONG_NORM)
                ? articleByFrameType.get(FrameType.STRONG_NORM)
                : articleByFrameType.get(FrameType.NORM);

        List<Article> selectedArticles = new ArrayList<>();

        if (valueArticle != null) {
            selectedArticles.add(valueArticle);
        }

        if (normArticle != null) {
            selectedArticles.add(normArticle);
        }

        // 4. 좌우 중 없는 쪽이 있을 경우 중립 추가
        Article neutralArticle = articleByFrameType.get(FrameType.NEUTRAL);
        if (selectedArticles.size() < 2 && neutralArticle != null) {
            selectedArticles.add(neutralArticle);
        }

        return new DailyFocusCacheDto(
                keywordId,
                keyword.getName(),
                keyword.getThumbnailUrl(),
                keywordService.getArticleCount(keywordId),
                selectedArticles.stream().map(this::toDailyFocusArticleDto).toList(),
                keyword.getCategory()
        );
    }

}
