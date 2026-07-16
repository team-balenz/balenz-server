package com.example.Balenz.keyword.service;

import com.example.Balenz.article.dto.RelatedArticlesDto;
import com.example.Balenz.article.entity.Article;
import com.example.Balenz.article.entity.FrameType;
import com.example.Balenz.article.repository.ArticleRepository;
import com.example.Balenz.article.service.ArticleDetailService;
import com.example.Balenz.global.exception.BaseException;
import com.example.Balenz.global.exception.ErrorCode;
import com.example.Balenz.keyword.dto.KeywordDetailDto;
import com.example.Balenz.keyword.dto.KeywordDto;
import com.example.Balenz.keyword.dto.ScopeSectionResponseDto;
import com.example.Balenz.keyword.entity.DominantFrameType;
import com.example.Balenz.keyword.entity.Keyword;
import com.example.Balenz.keyword.repository.KeywordRepository;
import com.example.Balenz.scrap.repository.UserKeywordScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
    private final ArticleDetailService articleDetailService;
    private final UserKeywordScrapRepository userKeywordScrapRepository;

    @Transactional
    public KeywordDetailDto getKeywordDetail(Long keywordId, Long userId) {
        Keyword keyword = keywordRepository.findById(keywordId).orElseThrow(
                () -> new BaseException(ErrorCode.KEYWORD_NOT_FOUND, "해당 id의 키워드를 찾을 수 없습니다."));

        // 조회수 증가
        keyword.increaseViewCount();

        // 프레임별 기사 개수 조회 및 편향 분포 계산
        ScopeSectionResponseDto.ArticleCountDto articleCount = keywordService.getArticleCount(keywordId);
        DominantFrameType dominantFrameType = keywordService.getDominantArticleFrameType(articleCount);
        int bias = keywordService.getBias(dominantFrameType, articleCount);

        // 인기 scope 조회
        List<KeywordDto> hotKeywords = keywordService.getHotKeywordDtos();

        // 프레임 타입별 메인 기사 (조회수가 가장 높은 기사) 조회
        KeywordDetailDto.MainArticlesDto mainArticles = getMainArticleDtos(keywordId);

        // 연관기사 조회 (메인 기사는 제외)
        Set<Long> mainArticleIds = getMainArticleIds(mainArticles);
        RelatedArticlesDto relatedArticlesDto = articleDetailService.getRelatedArticlesDto(keywordId, mainArticleIds);

        // 스크랩 여부 조회
        boolean isScraped = false;
        if (userId != null) {
            isScraped = userKeywordScrapRepository.existsByUser_IdAndKeyword_Id(userId, keywordId);
        }

        return KeywordDetailDto.builder()
                .id(keywordId)
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
                .hotKeywords(hotKeywords)
                .scraped(isScraped).build();
    }

    private Set<Long> getMainArticleIds(KeywordDetailDto.MainArticlesDto mainArticles) {
        return Stream.of(mainArticles.getValue(), mainArticles.getNeutral(), mainArticles.getNorm())
                .filter(Objects::nonNull)
                .map(RelatedArticlesDto.RelatedArticleDto::getId)
                .collect(Collectors.toSet());
    }

    /** 프레임 타입별로 메인 기사 반환 */
    private KeywordDetailDto.MainArticlesDto getMainArticleDtos(Long id) {
        Article mainStrongValueArticle = getMainArticleByKeywordAndFrameType(id, FrameType.STRONG_VALUE);
        Article mainValueArticle = getMainArticleByKeywordAndFrameType(id, FrameType.VALUE);
        Article mainNeutralArticle = getMainArticleByKeywordAndFrameType(id, FrameType.NEUTRAL);
        Article mainNormArticle = getMainArticleByKeywordAndFrameType(id, FrameType.NORM);
        Article mainStrongNormArticle = getMainArticleByKeywordAndFrameType(id, FrameType.STRONG_NORM);

        // VALUE 프레임 (STRONG_VALUE / VALUE) 메인 기사
        RelatedArticlesDto.RelatedArticleDto mainValueDto = getMainArticle(mainStrongValueArticle, mainValueArticle);

        // NEUTRAL 프레임 메인 기사
        RelatedArticlesDto.RelatedArticleDto mainNeutralDto = null;
        if (mainNeutralArticle != null) {
            mainNeutralDto = articleDetailService.toRelatedArticleDto(mainNeutralArticle);
        }

        // NORM 프레임 (STRONG_NORM / NORM) 메인 기사
        RelatedArticlesDto.RelatedArticleDto mainNormDto = getMainArticle(mainStrongNormArticle, mainNormArticle);

        return KeywordDetailDto.MainArticlesDto.builder()
                .value(mainValueDto)
                .neutral(mainNeutralDto)
                .norm(mainNormDto).build();
    }

    private Article getMainArticleByKeywordAndFrameType(Long keywordId, FrameType frameType) {
        return articleRepository.findByKeyword_IdAndFrameTypeOrderByViewCountDesc(
                        keywordId,
                        frameType,
                        PageRequest.of(0, 1))
                .stream().findFirst()
                .orElse(null);
    }

    /** 두 개의 기사 중 어떤 게 메인인지 결정 */
    private RelatedArticlesDto.RelatedArticleDto getMainArticle(Article article1, Article article2) {
        if (article1 != null && article2 != null) { // 둘 다 null이 아니면 둘 중 조회수 더 많은 것이 메인
            if (article1.getTotalViewCount() > article2.getTotalViewCount()) {
                return articleDetailService.toRelatedArticleDto(article1);
            } else {
                return articleDetailService.toRelatedArticleDto(article2);
            }
        } else if (article1 != null) {
            return articleDetailService.toRelatedArticleDto(article1);
        } else if (article2 != null) {
            return articleDetailService.toRelatedArticleDto(article2);
        }
        return null;
    }

}