package com.example.Balenz.keyword.controller;

import com.example.Balenz.global.response.BaseResponse;
import com.example.Balenz.keyword.dto.KeywordDetailDto;
import com.example.Balenz.keyword.dto.ScopeSectionResponseDto;
import com.example.Balenz.keyword.entity.Category;
import com.example.Balenz.keyword.service.KeywordDetailService;
import com.example.Balenz.keyword.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class KeywordController {

    private final KeywordService keywordService;
    private final KeywordDetailService keywordDetailService;

    @GetMapping("/ideology/keyword")
    public ResponseEntity<?> getScopeData(@RequestParam(required = false) Category category) {
        ScopeSectionResponseDto scopeSectionData = keywordService.getScopeSectionData(category);
        return ResponseEntity.ok().body(BaseResponse.success(scopeSectionData));
    }

    @GetMapping("/keyword/{keywordId}")
    public ResponseEntity<?> getKeywordDetail(@PathVariable Long keywordId) {
        KeywordDetailDto keywordDetail = keywordDetailService.getKeywordDetail(keywordId);
        return ResponseEntity.ok().body(BaseResponse.success(keywordDetail));
    }

}
