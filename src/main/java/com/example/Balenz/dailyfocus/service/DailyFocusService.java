package com.example.Balenz.dailyfocus.service;

import com.example.Balenz.article.entity.Article;
import com.example.Balenz.article.entity.FrameType;
import com.example.Balenz.article.repository.ArticleRepository;
import com.example.Balenz.dailyfocus.dto.DailyFocusArticleDto;
import com.example.Balenz.dailyfocus.dto.DailyFocusKeywordDto;
import com.example.Balenz.keyword.entity.Category;
import com.example.Balenz.keyword.entity.Keyword;
import com.example.Balenz.keyword.repository.KeywordRepository;
import com.example.Balenz.keyword.service.KeywordDetailService;
import com.example.Balenz.keyword.service.KeywordService;
import com.example.Balenz.scrap.service.KeywordScrapService;
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
public class DailyFocusService {

    private final ArticleRepository articleRepository;
    private final KeywordRepository keywordRepository;
    private final KeywordService keywordService;
    private final KeywordDetailService keywordDetailService;
    private final KeywordScrapService keywordScrapService;

    public List<DailyFocusKeywordDto> getDailyFocus(Long userId) {
        LocalDate serviceDate = keywordService.getCurrentServiceDate();

        List<DailyFocusKeywordDto> result = new ArrayList<>();
        for (Category category : Category.values()) {
            // 1. 카테고리별 가장 조회수가 높은 키워드 조회
            Keyword keyword = keywordRepository.findTopByCategoryAndServiceDateOrderByViewCountDescIdDesc(category, serviceDate).orElse(null);

            if (keyword == null) continue;

            Long keywordId = keyword.getId();

            // 2. 조회수 순으로 키워드 내 기사 전체 조회
            List<Article> articles = articleRepository.findByKeywordIdOrderByTotalViewCountDesc(keywordId);

            // 3. 프레임타입별 조회수가 가장 높은 기사 조회
            Map<FrameType, Article> articleByFrameType = articles.stream()
                    .collect(Collectors.toMap(
                            Article::getFrameType,
                            Function.identity(),
                            (first, second) -> first
                    ));

            // 4. 좌우별로 기사 1개씩 조회 - 양극단 우선
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

            // 5. 좌우 중 없는 쪽이 있을 경우 중립 추가
            Article neutralArticle = articleByFrameType.get(FrameType.NEUTRAL);
            if (selectedArticles.size() < 2 && neutralArticle != null) {
                selectedArticles.add(neutralArticle);
            }

            // 6. 스크랩 여부 확인
            boolean isScraped = keywordScrapService.isScraped(keywordId, userId);

            DailyFocusKeywordDto dailyFocusKeywordDto = new DailyFocusKeywordDto(
                    keywordId,
                    keyword.getName(),
                    keyword.getThumbnailUrl(),
                    keywordService.getArticleCount(keywordId),
                    selectedArticles.stream().map(this::toDailyFocusArticleDto).toList(),
                    isScraped
            );

            result.add(dailyFocusKeywordDto);

            // 7. 캐시에 저장

        }

        return result;
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

}
