package com.example.Balenz.internal.service;

import com.example.Balenz.article.dto.FrameTypeClassifiableDto;
import com.example.Balenz.article.dto.FrameTypeClassifyDto;
import com.example.Balenz.article.dto.KeywordExtractDto;
import com.example.Balenz.article.dto.SummaryDto;
import com.example.Balenz.article.entity.FrameType;
import com.example.Balenz.article.external.ClaudeApiClient;
import com.example.Balenz.article.external.prompt.FrameTypeClassifiablePrompt;
import com.example.Balenz.article.external.prompt.FrameTypeClassifyPrompt;
import com.example.Balenz.article.external.prompt.KeywordPrompt;
import com.example.Balenz.article.external.prompt.SummaryPrompt;
import com.example.Balenz.article.service.AdminArticleService;
import com.example.Balenz.global.exception.BaseException;
import com.example.Balenz.global.exception.ErrorCode;
import com.example.Balenz.internal.dto.TestResponseDto;
import com.example.Balenz.internal.entity.TestArticle;
import com.example.Balenz.internal.repository.TestArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestArticleRepository testArticleRepository;
    private final ClaudeApiClient claudeApiClient;
    private final AdminArticleService adminArticleService;

    public TestResponseDto testPrompt(Long articleId) {
        TestArticle testArticle = testArticleRepository.findById(articleId)
                .orElseThrow(() -> new BaseException(ErrorCode.ARTICLE_NOT_FOUND, "해당 id의 테스트 기사를 찾을 수 없습니다."));

        String title = testArticle.getTitle();
        String content = testArticle.getContent();

        // 키워드 추출
        KeywordExtractDto keywordExtractDto = claudeApiClient.getResponse(
                KeywordPrompt.create(title, content, List.of()),
                300L,
                KeywordExtractDto.class
        );

        // 기사 요약
        SummaryDto summaryDto = claudeApiClient.getResponse(
                SummaryPrompt.create(title, content),
                1024L,
                SummaryDto.class
        );

        String summary = summaryDto.summary();

        // 이념 분류 가능 여부 판별
        FrameTypeClassifiableDto frameTypeClassifiableDto = claudeApiClient.getResponse(
                FrameTypeClassifiablePrompt.create(content),
                300L,
                FrameTypeClassifiableDto.class
        );

        boolean isClassifiable = frameTypeClassifiableDto.isClassifiable();

        if (!isClassifiable) {
            return TestResponseDto.builder()
                    .keyword(keywordExtractDto.keyword())
                    .summary(summary)
                    .isClassifiable(false)
                    .frameType(FrameType.UNKNOWN).build();

        }

        FrameTypeClassifyDto frameTypeClassifyDto = claudeApiClient.getResponse(
                FrameTypeClassifyPrompt.create(content),
                1024L,
                FrameTypeClassifyDto.class
        );

        return TestResponseDto.builder()
                .keyword(keywordExtractDto.keyword())
                .summary(summary)
                .isClassifiable(true)
                .frameType(adminArticleService.toFrameType(frameTypeClassifyDto.ideology())).build();
    }

}
