package com.example.Balenz.article.controller;

import com.example.Balenz.article.dto.ArticleSaveRequestDto;
import com.example.Balenz.article.service.AdminArticleService;
import com.example.Balenz.global.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/article")
public class AdminArticleController {

    private final AdminArticleService adminArticleService;

    @PostMapping
    public ResponseEntity<?> saveArticle(@Valid @RequestBody ArticleSaveRequestDto articleSaveRequestDto) {
        adminArticleService.saveArticle(articleSaveRequestDto);
        return ResponseEntity.ok()
                .body(BaseResponse.success(null));
    }

}
