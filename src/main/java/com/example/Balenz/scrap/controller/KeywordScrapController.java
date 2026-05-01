package com.example.Balenz.scrap.controller;

import com.example.Balenz.global.response.BaseResponse;
import com.example.Balenz.global.security.CustomPrincipal;
import com.example.Balenz.scrap.service.KeywordScrapService;
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
public class KeywordScrapController {

    private final KeywordScrapService keywordScrapService;

    @PostMapping("/keyword/{keywordId}/scrap")
    public ResponseEntity<?> scrapKeyword(@PathVariable Long keywordId,
                                          @AuthenticationPrincipal CustomPrincipal customPrincipal) {
        keywordScrapService.scrapKeyword(keywordId, customPrincipal.getId());
        return ResponseEntity.ok().body(BaseResponse.success(null));
    }

}
