package com.example.Balenz.keyword.controller;

import com.example.Balenz.keyword.dto.ScopeSectionResponseDto;
import com.example.Balenz.keyword.entity.Category;
import com.example.Balenz.keyword.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class KeywordController {

    private final KeywordService keywordService;

    @GetMapping("/ideology/keyword")
    public ResponseEntity<?> getScopeData(@RequestParam(required = false) Category category) {
        ScopeSectionResponseDto scopeSectionData = keywordService.getScopeSectionData(category);
        return ResponseEntity.ok()
                .body(scopeSectionData);
    }

}
