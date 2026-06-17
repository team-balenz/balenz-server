package com.example.Balenz.search.service;

import com.example.Balenz.article.dto.SimpleArticleWithoutImageDto;
import com.example.Balenz.article.entity.Article;
import com.example.Balenz.article.repository.ArticleRepository;
import com.example.Balenz.article.service.ArticleService;
import com.example.Balenz.keyword.entity.Keyword;
import com.example.Balenz.keyword.repository.KeywordRepository;
import com.example.Balenz.keyword.service.KeywordService;
import com.example.Balenz.search.dto.SearchResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final KeywordRepository keywordRepository;
    private final ArticleRepository articleRepository;
    private final KeywordService keywordService;
    private final ArticleService articleService;

    public SearchResponseDto search(String query) {
        List<Keyword> keywords = keywordRepository.searchByName(query);
        List<Article> articles = articleRepository.searchByTitle(query);

        return new SearchResponseDto(
                keywordService.getKeywordDtos(keywords),
                toSimpleArticleDtoWithoutImageDtos(articles)
        );
    }

    private List<SimpleArticleWithoutImageDto> toSimpleArticleDtoWithoutImageDtos(List<Article> articles) {
        List<SimpleArticleWithoutImageDto> simpleArticleWithoutImageDtos = new ArrayList<>();
        for (Article article : articles) {
            simpleArticleWithoutImageDtos.add(
                    articleService.toSimpleArticleDtoWithoutImage(article)
            );
        }

        return simpleArticleWithoutImageDtos;
    }

}
