package com.example.Balenz.scrap.controller;

import com.example.Balenz.global.response.BaseResponse;
import com.example.Balenz.global.security.CustomPrincipal;
import com.example.Balenz.scrap.service.ArticleScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ArticleScrapController {

    private final ArticleScrapService articleScrapService;

    @PostMapping("/article/{articleId}/scrap")
    public ResponseEntity<?> scrapArticle(@PathVariable Long articleId,
                                          @AuthenticationPrincipal CustomPrincipal customPrincipal) {
        articleScrapService.scrapArticle(articleId, customPrincipal.getId());
        return ResponseEntity.ok().body(BaseResponse.success(null));
    }

}
