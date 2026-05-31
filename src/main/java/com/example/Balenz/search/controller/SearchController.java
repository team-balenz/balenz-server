package com.example.Balenz.search.controller;

import com.example.Balenz.global.response.BaseResponse;
import com.example.Balenz.search.dto.SearchResponseDto;
import com.example.Balenz.search.service.SearchService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<?> search(@RequestParam @NotBlank String query) {
        SearchResponseDto result = searchService.search(query);
        return ResponseEntity.ok().body(BaseResponse.success(result));
    }

}
