package com.example.Balenz.article.service;

import com.example.Balenz.article.dto.*;
import com.example.Balenz.article.entity.Article;
import com.example.Balenz.article.entity.FrameType;
import com.example.Balenz.article.entity.NewsAgency;
import com.example.Balenz.article.external.ClaudeApiClient;
import com.example.Balenz.article.external.prompt.FrameTypeClassifiablePrompt;
import com.example.Balenz.article.external.prompt.FrameTypeClassifyPrompt;
import com.example.Balenz.article.external.prompt.KeywordPrompt;
import com.example.Balenz.article.external.prompt.SummaryPrompt;
import com.example.Balenz.article.repository.ArticleRepository;
import com.example.Balenz.article.repository.NewsAgencyRepository;
import com.example.Balenz.global.email.EmailService;
import com.example.Balenz.global.exception.BaseException;
import com.example.Balenz.global.exception.ErrorCode;
import com.example.Balenz.keyword.entity.Category;
import com.example.Balenz.keyword.entity.Keyword;
import com.example.Balenz.keyword.repository.KeywordRepository;
import com.example.Balenz.keyword.service.KeywordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminArticleService {

    private final NewsAgencyRepository newsAgencyRepository;
    private final KeywordRepository keywordRepository;
    private final ArticleRepository articleRepository;
    private final KeywordService keywordService;
    private final EmailService emailService;
    private final ClaudeApiClient claudeApiClient;

    @Transactional
    public void saveArticle(ArticleSaveRequestDto articleSaveRequestDto) {
        String title = articleSaveRequestDto.getTitle();
        String content = articleSaveRequestDto.getContent();

        // 1. 언론사 조회 - 없으면 생성
        NewsAgency newsAgency = newsAgencyRepository.findByName(articleSaveRequestDto.getNewsAgencyName())
                .orElseGet(() -> newsAgencyRepository.save(
                        NewsAgency.builder().name(articleSaveRequestDto.getNewsAgencyName()).build()
                ));

        // 2. 키워드 추출
        LocalDate preparedServiceDate = keywordService.getPreparedServiceDate();

        List<String> existingKeywords = keywordRepository
                .findByServiceDate(preparedServiceDate)
                .stream()
                .map(Keyword::getName).toList();

        KeywordExtractDto keywordExtractDto = claudeApiClient.getResponse(
                KeywordPrompt.create(title, content, existingKeywords),
                300L,
                KeywordExtractDto.class
        );

        Category category = toCategory(keywordExtractDto.category());

        Keyword keyword = keywordRepository.findByNameAndCategoryAndServiceDate(keywordExtractDto.keyword(), category, preparedServiceDate)
                .orElseGet(() -> keywordRepository.save(
                        Keyword.builder()
                                .name(keywordExtractDto.keyword())
                                .thumbnailUrl(articleSaveRequestDto.getImageUrl())
                                .category(category)
                                .serviceDate(preparedServiceDate).build()
                ));

        // 3. 기사 요약
        SummaryDto summaryDto = claudeApiClient.getResponse(
                SummaryPrompt.create(title, content),
                1024L,
                SummaryDto.class
        );


        String summary = summaryDto.summary();

        // 4. 프레임타입 UNKNOWN으로 article 저장
        Article article = articleRepository.save(Article.builder()
                .title(title)
                .articleUrl(articleSaveRequestDto.getArticleUrl())
                .imageUrl(articleSaveRequestDto.getImageUrl())
                .summary(summary)
                .frameType(FrameType.UNKNOWN)
                .publishedAt(articleSaveRequestDto.getPublishedAt())
                .newsAgency(newsAgency)
                .keyword(keyword).build());

        // 5. 이념 분류 가능 여부 판별
        FrameTypeClassifiableDto frameTypeClassifiableDto = claudeApiClient.getResponse(
                FrameTypeClassifiablePrompt.create(content),
                300L,
                FrameTypeClassifiableDto.class
        );


        boolean isClassifiable = frameTypeClassifiableDto.isClassifiable();

        // 5-1. 이념 분류 불가할 경우 수동 추출 요청 이메일 전송
        if (!isClassifiable) {
            try {
                emailService.sendEmail(
                        "[이념 분류 실패] 수동 이념 분류가 필요합니다. #" + article.getId(),
                        """
                        - 기사 ID: %d
                        - 기사 제목: %s
                        - 기사 내용: %s
                        
                        - 업로드 예정일: %s
                        """.formatted(
                                article.getId(),
                                article.getTitle(),
                                articleSaveRequestDto.getContent(),
                                preparedServiceDate
                        )
                );
            } catch (Exception e) {
                log.error("수동 이념 분류 요청 이메일 전송 실패 - articleId={}", article.getId(), e);
            }
            return;
        }

        // 5-2. 이념 분류 가능한 경우 이념 추출
        FrameTypeClassifyDto frameTypeClassifyDto = claudeApiClient.getResponse(
                FrameTypeClassifyPrompt.create(content),
                1024L,
                FrameTypeClassifyDto.class
        );

        FrameType frameType = toFrameType(frameTypeClassifyDto.ideology());
        article.updateFrameType(frameType);
    }

    @Transactional
    public void updateArticleFrameType(FrameTypeUpdateRequestDto frameTypeUpdateRequestDto) {
        Article article = articleRepository.findById(frameTypeUpdateRequestDto.getArticleId()).orElseThrow(
                () -> new BaseException(ErrorCode.ARTICLE_NOT_FOUND, "해당 id의 기사가 존재하지 않습니다."));

        article.updateFrameType(frameTypeUpdateRequestDto.getFrameType());
    }

    public FrameType toFrameType(String ideology) {
        if (ideology == null || ideology.isBlank()) {
            throw new BaseException(ErrorCode.EXTERNAL_API_ERROR, "ideology 값이 비어있습니다.");
        }

        return switch (ideology) {
            case "SL" -> FrameType.STRONG_VALUE;
            case "L" -> FrameType.VALUE;
            case "C" -> FrameType.NEUTRAL;
            case "R" -> FrameType.NORM;
            case "SR" -> FrameType.STRONG_NORM;
            default -> throw new BaseException(ErrorCode.EXTERNAL_API_ERROR, "잘못된 ideology입니다. - " + ideology);
        };
    }

    private Category toCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new BaseException(ErrorCode.EXTERNAL_API_ERROR, "category 값이 비어있습니다.");
        }

        return switch (category) {
            case "정치" -> Category.POLITICS;
            case "경제" -> Category.ECONOMY;
            case "사회" -> Category.SOCIETY;
            case "세계" -> Category.WORLD;
            case "문화" -> Category.CULTURE;
            case "기술" -> Category.TECHNOLOGY;
            default -> throw new BaseException(ErrorCode.EXTERNAL_API_ERROR, "잘못된 category입니다. - " + category);
        };
    }

}
