package com.example.Balenz.article.service;

import com.example.Balenz.article.dto.ArticleSaveRequestDto;
import com.example.Balenz.article.dto.FrameTypeUpdateRequestDto;
import com.example.Balenz.article.entity.Article;
import com.example.Balenz.article.entity.FrameType;
import com.example.Balenz.article.entity.NewsAgency;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminArticleService {

    private final NewsAgencyRepository newsAgencyRepository;
    private final KeywordRepository keywordRepository;
    private final ArticleRepository articleRepository;
    private final KeywordService keywordService;
    private final EmailService emailService;

    @Transactional
    public void saveArticle(ArticleSaveRequestDto articleSaveRequestDto) {
        // 1. 언론사 조회 - 없으면 생성
        NewsAgency newsAgency = newsAgencyRepository.findByName(articleSaveRequestDto.getNewsAgencyName())
                .orElseGet(() -> newsAgencyRepository.save(
                        NewsAgency.builder().name(articleSaveRequestDto.getNewsAgencyName()).build()
                ));

        // 2. 키워드 추출
        // TODO : 키워드 및 카테고리 추출 로직 연동
        String keywordName = "경찰청";
        Category category = Category.SOCIETY;

        LocalDate preparedServiceDate = keywordService.getPreparedServiceDate();

        Keyword keyword = keywordRepository.findByNameAndCategoryAndServiceDate(keywordName, category, preparedServiceDate)
                .orElseGet(() -> keywordRepository.save(
                        Keyword.builder()
                                .name(keywordName)
                                .thumbnailUrl(articleSaveRequestDto.getImageUrl())
                                .category(category)
                                .serviceDate(preparedServiceDate).build()
                ));

        // 3. 기사 요약
        // TODO : 기사 요약 연동
        String summary = "요약내용";

        // 4. 프레임타입 UNKNOWN으로 article 저장
        Article article = articleRepository.save(Article.builder()
                .title(articleSaveRequestDto.getTitle())
                .articleUrl(articleSaveRequestDto.getArticleUrl())
                .imageUrl(articleSaveRequestDto.getImageUrl())
                .summary(summary)
                .frameType(FrameType.UNKNOWN)
                .publishedAt(articleSaveRequestDto.getPublishedAt())
                .newsAgency(newsAgency)
                .keyword(keyword).build());

        // 5. 이념 분류 가능 여부 판별
        // TODO : 연동
        String result = "N";

        // 5-1. 이념 분류 불가할 경우 수동 추출 요청 이메일 전송
        if ("N".equals(result)) {
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
        // TODO : 연동
        FrameType frameType = FrameType.NEUTRAL;
        article.updateFrameType(frameType);
    }

    @Transactional
    public void updateArticleFrameType(FrameTypeUpdateRequestDto frameTypeUpdateRequestDto) {
        Article article = articleRepository.findById(frameTypeUpdateRequestDto.getArticleId()).orElseThrow(
                () -> new BaseException(ErrorCode.ARTICLE_NOT_FOUND, "해당 id의 기사가 존재하지 않습니다."));

        article.updateFrameType(frameTypeUpdateRequestDto.getFrameType());
    }

}
