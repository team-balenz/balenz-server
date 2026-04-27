package com.example.Balenz.article.service;

import com.example.Balenz.article.dto.ArticleDetailDto;
import com.example.Balenz.article.dto.RelatedArticlesDto;
import com.example.Balenz.article.entity.Article;
import com.example.Balenz.article.entity.FrameType;
import com.example.Balenz.article.repository.ArticleRepository;
import com.example.Balenz.global.exception.BaseException;
import com.example.Balenz.global.exception.ErrorCode;
import com.example.Balenz.keyword.dto.ScopeSectionResponseDto;
import com.example.Balenz.keyword.entity.Keyword;
import com.example.Balenz.keyword.service.KeywordService;
import com.example.Balenz.user.entity.Ideology;
import com.example.Balenz.user.entity.User;
import com.example.Balenz.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final KeywordService keywordService;
    private final AuthService authService;

    @Transactional
    public ArticleDetailDto getArticleDetail(Long articleId, Long userId) {
        User user = authService.getCurrentUser(userId);


        Article article = articleRepository.findById(articleId).orElseThrow(
                () -> new BaseException(ErrorCode.ARTICLE_NOT_FOUND, "해당 id의 기사를 찾을 수 없습니다."));

        // user의 ideology에 따라 기사 조회수 증가
        Ideology ideology = user.getIdeology();
        if (ideology != null) {
            switch (ideology) {
                case VALUE -> article.increaseValueUserViewCount();
                case NEUTRAL -> article.increaseNeutralUserViewCount();
                case NORM -> article.increaseNormUserViewCount();
            }
        }

        // 연관기사 조회 -> DTO 생성
        Keyword keyword = article.getKeyword();
        Set<Long> excludeArticleIds = Set.of(articleId);
        RelatedArticlesDto relatedArticlesDto = getRelatedArticlesDto(keyword.getId(), excludeArticleIds);

        // 인기 scope 조회
        List<ScopeSectionResponseDto.KeywordDto> hotKeywords = keywordService.getHotKeywordDtos();

        return ArticleDetailDto.builder()
                .title(article.getTitle())
                .newsAgencyName(article.getNewsAgency().getName())
                .publishedAt(article.getPublishedAt())
                .frameType(article.getFrameType())
                .summary(article.getSummary())
                .articleUrl(article.getArticleUrl())
                .relatedArticles(relatedArticlesDto)
                .hotKeywords(hotKeywords).build();
    }

    public RelatedArticlesDto getRelatedArticlesDto(Long keywordId, Set<Long> excludeArticleIds) {
        List<Article> strongValue = articleRepository.findTop4ByKeyword_IdAndFrameTypeOrderByPublishedAtDesc(keywordId, FrameType.STRONG_VALUE);
        List<Article> value = articleRepository.findTop4ByKeyword_IdAndFrameTypeOrderByPublishedAtDesc(keywordId, FrameType.VALUE);
        List<Article> neutral = articleRepository.findTop4ByKeyword_IdAndFrameTypeOrderByPublishedAtDesc(keywordId, FrameType.NEUTRAL);
        List<Article> norm = articleRepository.findTop4ByKeyword_IdAndFrameTypeOrderByPublishedAtDesc(keywordId, FrameType.NORM);
        List<Article> strongNorm = articleRepository.findTop4ByKeyword_IdAndFrameTypeOrderByPublishedAtDesc(keywordId, FrameType.STRONG_NORM);

        List<RelatedArticlesDto.RelatedArticleDto> valueRelatedArticles = Stream
                .concat(value.stream(), strongValue.stream())
                .filter(article -> !excludeArticleIds.contains(article.getId()))
                .map(this::toRelatedArticleDto)
                .collect(Collectors.toList());

        List<RelatedArticlesDto.RelatedArticleDto> normRelatedArticles = Stream
                .concat(norm.stream(), strongNorm.stream())
                .filter(article -> !excludeArticleIds.contains(article.getId()))
                .map(this::toRelatedArticleDto)
                .collect(Collectors.toList());

        List<RelatedArticlesDto.RelatedArticleDto> neutralRelatedArticles = neutral.stream()
                .filter(article -> !excludeArticleIds.contains(article.getId()))
                .map(this::toRelatedArticleDto).collect(Collectors.toList());

        // 전체 - Value 3 Neutral 4 Norm 3 비율에 맞춰서 생성
        List<RelatedArticlesDto.RelatedArticleDto> allRelatedArticles = Stream.of(
                pick(valueRelatedArticles, 3).stream(),
                pick(neutralRelatedArticles, 4).stream(),
                pick(normRelatedArticles, 3).stream()
                ).flatMap(s -> s)
                .collect(Collectors.toList());

        if (allRelatedArticles.size() < 10) {
            int remaining = 10 - allRelatedArticles.size();

            // 전체 후보 풀 만들기
            List<RelatedArticlesDto.RelatedArticleDto> pool = Stream.of(
                            valueRelatedArticles,
                            neutralRelatedArticles,
                            normRelatedArticles)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            // 이미 allRelatedArticles에 들어가있는 것 제외
            pool.removeIf(
                    a -> allRelatedArticles.stream()
                            .anyMatch(s -> s.getId().equals(a.getId()))
            );

            // 부족한 만큼 추가
            allRelatedArticles.addAll(pick(pool, remaining));
        }

        return RelatedArticlesDto.builder()
                .all(allRelatedArticles)
                .value(valueRelatedArticles)
                .neutral(neutralRelatedArticles)
                .norm(normRelatedArticles).build();
    }

    public RelatedArticlesDto.RelatedArticleDto toRelatedArticleDto(Article article) {
        return RelatedArticlesDto.RelatedArticleDto.builder()
                .id(article.getId())
                .title(article.getTitle())
                .newsAgencyName(article.getNewsAgency().getName())
                .publishedAt(article.getPublishedAt())
                .frameType(article.getFrameType())
                .summary(article.getSummary()).build();
    }

    private List<RelatedArticlesDto.RelatedArticleDto> shuffle(List<RelatedArticlesDto.RelatedArticleDto> list) {
        Collections.shuffle(list);
        return list;
    }

    /** 리스트 shuffle 후 count 개수만큼 뽑기 */
    private List<RelatedArticlesDto.RelatedArticleDto> pick(List<RelatedArticlesDto.RelatedArticleDto> list, int count) {
        ArrayList<RelatedArticlesDto.RelatedArticleDto> copy = new ArrayList<>(list);
        Collections.shuffle(copy);
        return copy;
    }

}