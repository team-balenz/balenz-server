package com.example.Balenz.article.controller;

import com.example.Balenz.article.dto.ArticleDetailDto;
import com.example.Balenz.article.service.ArticleService;
import com.example.Balenz.global.response.BaseResponse;
import com.example.Balenz.global.security.CustomPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<?> getArticleDetail(@PathVariable Long articleId,
                                              @AuthenticationPrincipal CustomPrincipal customPrincipal) {
        ArticleDetailDto articleDetail = articleService.getArticleDetail(articleId, customPrincipal.getId());
        return ResponseEntity.ok().body(BaseResponse.success(articleDetail));
    }

}
