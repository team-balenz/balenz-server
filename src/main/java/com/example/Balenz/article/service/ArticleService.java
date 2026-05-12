package com.example.Balenz.article.service;

import com.example.Balenz.article.dto.ArticlesByIdeologyInterestDto;
import com.example.Balenz.article.dto.SimpleArticleDto;
import com.example.Balenz.article.entity.Article;
import com.example.Balenz.article.repository.ArticleRepository;
import com.example.Balenz.keyword.service.HotKeywordService;
import com.example.Balenz.keyword.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final KeywordService keywordService;
    private final HotKeywordService hotKeywordService;

    public ArticlesByIdeologyInterestDto getArticlesByIdeologyInterest() {
        LocalDate serviceDate = keywordService.getCurrentServiceDate();
        List<Article> top8ValueUserViewCountArticles = articleRepository.findTop8ByKeyword_ServiceDateOrderByValueUserViewCountDesc(serviceDate);
        List<Article> top8NormUserViewCountArticles = articleRepository.findTop8ByKeyword_ServiceDateOrderByNormUserViewCountDesc(serviceDate);

        List<SimpleArticleDto> valueInterestArticles = top8ValueUserViewCountArticles.stream()
                .map(this::toSimpleArticleDtoWithoutImage)
                .toList();
        List<SimpleArticleDto> normInterestArticles = top8NormUserViewCountArticles.stream()
                .map(this::toSimpleArticleDtoWithoutImage)
                .toList();

        return ArticlesByIdeologyInterestDto.builder()
                .valueInterestArticles(valueInterestArticles)
                .normInterestArticles(normInterestArticles).build();
    }

    private SimpleArticleDto toSimpleArticleDtoWithoutImage(Article article) {
        return SimpleArticleDto.builder()
                .id(article.getId())
                .title(article.getTitle())
                .newsAgencyName(article.getNewsAgency().getName())
                .frameType(article.getFrameType())
                .imageUrl(null).build();
    }

}
