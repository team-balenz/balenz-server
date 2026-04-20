package com.example.Balenz.article.service;

import com.example.Balenz.article.dto.ArticleSaveRequestDto;
import com.example.Balenz.article.entity.Article;
import com.example.Balenz.article.entity.FrameType;
import com.example.Balenz.article.entity.NewsAgency;
import com.example.Balenz.article.repository.ArticleRepository;
import com.example.Balenz.article.repository.NewsAgencyRepository;
import com.example.Balenz.keyword.entity.Category;
import com.example.Balenz.keyword.entity.Keyword;
import com.example.Balenz.keyword.repository.KeywordRepository;
import com.example.Balenz.keyword.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AdminArticleService {

    private final NewsAgencyRepository newsAgencyRepository;
    private final KeywordRepository keywordRepository;
    private final ArticleRepository articleRepository;
    private final KeywordService keywordService;

    @Transactional
    public void saveArticle(ArticleSaveRequestDto articleSaveRequestDto) {
        // 1. 프레임별 점수 계산
        // TODO : 점수 계산 연동
        Double valueScore = 0.038;
        Double normScore = 0.012;

        // 2. 프레임 타입 지정
        // TODO : 프레임 타입 지정 기준 논의 후 반영
        FrameType frameType;
        // intensity 작으면 약한 프레임 기사
        Double intensity = valueScore + normScore;
        // balanceRatio 작으면 중립 기사
        Double balanceRatio = Math.abs(valueScore - normScore) / intensity;
        if (intensity < 0.03 || balanceRatio < 0.2) {
            frameType = FrameType.NEUTRAL;
        } else {
            frameType = valueScore > normScore ? FrameType.VALUE : FrameType.NORM;
        }

        // 3. 기사 요약
        // TODO : 기사 요약 연동

        // 4. 언론사 조회 - 없으면 생성
        NewsAgency newsAgency = newsAgencyRepository.findByName(articleSaveRequestDto.getNewsAgencyName())
                .orElseGet(() -> newsAgencyRepository.save(
                        NewsAgency.builder().name(articleSaveRequestDto.getNewsAgencyName()).build()
                ));

        // 5. 키워드 추출
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

        articleRepository.save(Article.builder()
                .title(articleSaveRequestDto.getTitle())
                .articleUrl(articleSaveRequestDto.getArticleUrl())
                .imageUrl(articleSaveRequestDto.getImageUrl())
                .summary("요약요약")
                .valueScore(valueScore)
                .normScore(normScore)
                .frameType(frameType)
                .publishedAt(articleSaveRequestDto.getPublishedAt())
                .newsAgency(newsAgency)
                .keyword(keyword).build());
    }

}
