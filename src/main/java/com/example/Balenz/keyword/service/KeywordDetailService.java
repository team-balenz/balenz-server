package com.example.Balenz.keyword.service;

import com.example.Balenz.article.dto.RelatedArticlesDto;
import com.example.Balenz.article.entity.Article;
import com.example.Balenz.article.entity.FrameType;
import com.example.Balenz.article.repository.ArticleRepository;
import com.example.Balenz.article.service.ArticleService;
import com.example.Balenz.global.exception.BaseException;
import com.example.Balenz.global.exception.ErrorCode;
import com.example.Balenz.keyword.dto.KeywordDetailDto;
import com.example.Balenz.keyword.dto.ScopeSectionResponseDto;
import com.example.Balenz.keyword.entity.DominantFrameType;
import com.example.Balenz.keyword.entity.Keyword;
import com.example.Balenz.keyword.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class KeywordDetailService {

    private final KeywordRepository keywordRepository;
    private final ArticleRepository articleRepository;
    private final KeywordService keywordService;
    private final ArticleService articleService;

    @Transactional
    public KeywordDetailDto getKeywordDetail(Long id) {
        Keyword keyword = keywordRepository.findById(id).orElseThrow(
                () -> new BaseException(ErrorCode.KEYWORD_NOT_FOUND, "해당 id의 키워드를 찾을 수 없습니다."));

        // 조회수 증가
        keyword.increaseViewCount();

        // 프레임별 기사 개수 조회 및 편향 분포 계산
        ScopeSectionResponseDto.ArticleCountDto articleCount = keywordService.getArticleCount(id);
        DominantFrameType dominantFrameType = keywordService.getDominantArticleFrameType(articleCount);
        int bias = keywordService.getBias(dominantFrameType, articleCount);

        // 인기 scope 조회
        List<ScopeSectionResponseDto.KeywordDto> hotKeywords = keywordService.getHotKeywordDtos();

        // 프레임 타입별 메인 기사 (조회수가 가장 높은 기사) 조회
        KeywordDetailDto.MainArticlesDto mainArticles = getMainArticleDtos(id);

        // 연관기사 조회 (메인 기사는 제외)
        Set<Long> mainArticleIds = getMainArticleIds(mainArticles);
        RelatedArticlesDto relatedArticlesDto = articleService.getRelatedArticlesDto(id, mainArticleIds);

        return KeywordDetailDto.builder()
                .id(id)
                .name(keyword.getName())
                .imageUrl(keyword.getThumbnailUrl())
                .date(keyword.getServiceDate())
                .viewCount(keyword.getViewCount())
                .keywordSummary(keyword.getSummary())
                .articleCount(articleCount)
                .bias(bias)
                .dominantFrameType(dominantFrameType)
                .mainArticles(mainArticles)
                .relatedArticles(relatedArticlesDto)
                .hotKeywords(hotKeywords).build();
    }

    private Set<Long> getMainArticleIds(KeywordDetailDto.MainArticlesDto mainArticles) {
        return Stream.of(mainArticles.getValue(), mainArticles.getNeutral(), mainArticles.getNorm())
                .filter(Objects::nonNull)
                .map(RelatedArticlesDto.RelatedArticleDto::getId)
                .collect(Collectors.toSet());
    }

    /** 프레임 타입별로 메인 기사 반환 */
    private KeywordDetailDto.MainArticlesDto getMainArticleDtos(Long id) {
        Article mainStrongValueArticle = articleRepository.findTopByKeyword_IdAndFrameTypeOrderByViewCountDesc(id, FrameType.STRONG_VALUE.name()).orElse(null);
        Article mainValueArticle = articleRepository.findTopByKeyword_IdAndFrameTypeOrderByViewCountDesc(id, FrameType.VALUE.name()).orElse(null);
        Article mainNeutralArticle = articleRepository.findTopByKeyword_IdAndFrameTypeOrderByViewCountDesc(id, FrameType.NEUTRAL.name()).orElse(null);
        Article mainNormArticle = articleRepository.findTopByKeyword_IdAndFrameTypeOrderByViewCountDesc(id, FrameType.NORM.name()).orElse(null);
        Article mainStrongNormArticle = articleRepository.findTopByKeyword_IdAndFrameTypeOrderByViewCountDesc(id, FrameType.STRONG_NORM.name()).orElse(null);

        // VALUE 프레임 (STRONG_VALUE / VALUE) 메인 기사
        RelatedArticlesDto.RelatedArticleDto mainValueDto = getMainArticle(mainStrongValueArticle, mainValueArticle);

        // NEUTRAL 프레임 메인 기사
        RelatedArticlesDto.RelatedArticleDto mainNeutralDto = null;
        if (mainNeutralArticle != null) {
            mainNeutralDto = articleService.toRelatedArticleDto(mainNeutralArticle);
        }

        // NORM 프레임 (STRONG_NORM / NORM) 메인 기사
        RelatedArticlesDto.RelatedArticleDto mainNormDto = getMainArticle(mainStrongNormArticle, mainNormArticle);

        return KeywordDetailDto.MainArticlesDto.builder()
                .value(mainValueDto)
                .neutral(mainNeutralDto)
                .norm(mainNormDto).build();
    }

    /** 두 개의 기사 중 어떤 게 메인인지 결정 */
    private RelatedArticlesDto.RelatedArticleDto getMainArticle(Article article1, Article article2) {
        if (article1 != null && article2 != null) { // 둘 다 null이 아니면 둘 중 조회수 더 많은 것이 메인
            if (article1.getTotalViewCount() > article2.getTotalViewCount()) {
                return articleService.toRelatedArticleDto(article1);
            } else {
                return articleService.toRelatedArticleDto(article2);
            }
        } else if (article1 != null) {
            return articleService.toRelatedArticleDto(article1);
        } else if (article2 != null) {
            return articleService.toRelatedArticleDto(article2);
        }
        return null;
    }

}