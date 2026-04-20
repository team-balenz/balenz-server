package com.example.Balenz.keyword.controller;

import com.example.Balenz.global.response.BaseResponse;
import com.example.Balenz.keyword.dto.KeywordSaveRequestDto;
import com.example.Balenz.keyword.service.AdminKeywordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/keyword")
public class AdminKeywordController {

    private final AdminKeywordService adminKeywordService;

    @PostMapping
    public ResponseEntity<?> saveKeyword(@Valid @RequestBody KeywordSaveRequestDto keywordSaveRequestDto) {
        adminKeywordService.saveKeyword(keywordSaveRequestDto);
        return ResponseEntity.ok()
                .body(BaseResponse.success(null));
    }

}
