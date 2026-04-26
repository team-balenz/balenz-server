package com.example.Balenz.article.controller;

import com.example.Balenz.article.dto.ArticleDetailDto;
import com.example.Balenz.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/article")
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/{articleId}")
    public ResponseEntity<?> getArticleDetail(@PathVariable Long articleId) {
        ArticleDetailDto articleDetail = articleService.getArticleDetail(articleId);
        return ResponseEntity.ok().body(articleDetail);
    }

}
