package com.example.Balenz.article.service;

import com.example.Balenz.article.dto.ArticlesByIdeologyDto;
import com.example.Balenz.article.dto.ArticlesByIdeologyInterestDto;
import com.example.Balenz.article.dto.SimpleArticleWithoutImageDto;
import com.example.Balenz.article.entity.Article;
import com.example.Balenz.article.entity.FrameType;
import com.example.Balenz.article.repository.ArticleRepository;
import com.example.Balenz.keyword.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private static final int ARTICLE_LIMIT = 10;
    private static final int CANDIDATE_LIMIT = 30;

    private final ArticleRepository articleRepository;
    private final KeywordService keywordService;

    public ArticlesByIdeologyInterestDto getArticlesByIdeologyInterest() {
        LocalDate serviceDate = keywordService.getCurrentServiceDate();
        List<Article> top8ValueUserViewCountArticles = articleRepository.findTop8ByKeyword_ServiceDateOrderByValueUserViewCountDesc(serviceDate);
        List<Article> top8NormUserViewCountArticles = articleRepository.findTop8ByKeyword_ServiceDateOrderByNormUserViewCountDesc(serviceDate);

        List<SimpleArticleWithoutImageDto> valueInterestArticles = top8ValueUserViewCountArticles.stream()
                .map(this::toSimpleArticleDtoWithoutImage)
                .toList();
        List<SimpleArticleWithoutImageDto> normInterestArticles = top8NormUserViewCountArticles.stream()
                .map(this::toSimpleArticleDtoWithoutImage)
                .toList();

        return ArticlesByIdeologyInterestDto.builder()
                .valueInterestArticles(valueInterestArticles)
                .normInterestArticles(normInterestArticles).build();
    }

    public ArticlesByIdeologyDto getArticlesByIdeology() {
        LocalDate serviceDate = keywordService.getCurrentServiceDate();

        List<Article> valueArticleCandidates = articleRepository.findByKeyword_ServiceDateAndFrameTypeIn(serviceDate,
                List.of(FrameType.STRONG_VALUE, FrameType.VALUE),
                PageRequest.of(0, CANDIDATE_LIMIT));
        List<Article> normArticleCandidates = articleRepository.findByKeyword_ServiceDateAndFrameTypeIn(serviceDate,
                List.of(FrameType.STRONG_NORM, FrameType.NORM),
                PageRequest.of(0, CANDIDATE_LIMIT));
        List<SimpleArticleWithoutImageDto> neutralArticles = articleRepository.findByKeyword_ServiceDateAndFrameTypeIn(serviceDate,
                        List.of(FrameType.NEUTRAL),
                        PageRequest.of(0, ARTICLE_LIMIT))
                .stream()
                .map(this::toSimpleArticleDtoWithoutImage).toList();

        return ArticlesByIdeologyDto.builder()
                .value(getRandomArticlesFromCandidates(valueArticleCandidates))
                .neutral(neutralArticles)
                .norm(getRandomArticlesFromCandidates(normArticleCandidates)).build();
    }

    private List<SimpleArticleWithoutImageDto> getRandomArticlesFromCandidates(List<Article> articleCandidates) {
        List<Article> shuffledArticles = new ArrayList<>(articleCandidates);
        Collections.shuffle(shuffledArticles);
        return shuffledArticles.stream()
                .limit(ARTICLE_LIMIT)
                .map(this::toSimpleArticleDtoWithoutImage).toList();
    }

    private SimpleArticleWithoutImageDto toSimpleArticleDtoWithoutImage(Article article) {
        return SimpleArticleWithoutImageDto.builder()
                .id(article.getId())
                .title(article.getTitle())
                .newsAgencyName(article.getNewsAgency().getName())
                .frameType(article.getFrameType()).build();
    }

}
