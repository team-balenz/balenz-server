package com.example.Balenz.dailyfocus.controller;

import com.example.Balenz.dailyfocus.dto.DailyFocusKeywordDto;
import com.example.Balenz.dailyfocus.service.DailyFocusService;
import com.example.Balenz.global.response.BaseResponse;
import com.example.Balenz.global.security.CustomPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/daily-focus")
public class DailyFocusController {

    private final DailyFocusService dailyFocusService;

    @GetMapping
    public ResponseEntity<?> getDailyFocus(@AuthenticationPrincipal CustomPrincipal customPrincipal) {
        Long userId = customPrincipal != null ? customPrincipal.getId() : null;
        List<DailyFocusKeywordDto> dailyFocus = dailyFocusService.getDailyFocus(userId);
        return ResponseEntity.ok(BaseResponse.success(dailyFocus));
    }

}
