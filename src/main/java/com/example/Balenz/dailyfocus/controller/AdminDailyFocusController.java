package com.example.Balenz.dailyfocus.controller;

import com.example.Balenz.dailyfocus.dto.DailyFocusKeywordDto;
import com.example.Balenz.dailyfocus.dto.DailyFocusUpdateRequestDto;
import com.example.Balenz.dailyfocus.service.AdminDailyFocusService;
import com.example.Balenz.global.response.BaseResponse;
import com.example.Balenz.global.security.CustomPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/daily-focus")
public class AdminDailyFocusController {

    private final AdminDailyFocusService adminDailyFocusService;

    @PutMapping
    public ResponseEntity<?> updateDailyFocusKeyword(@Valid @RequestBody DailyFocusUpdateRequestDto updateRequestDto,
                                                     @AuthenticationPrincipal CustomPrincipal customPrincipal) {
        List<DailyFocusKeywordDto> dailyFocus = adminDailyFocusService.updateDailyFocusKeyword(updateRequestDto, customPrincipal.getId());
        return ResponseEntity.ok(BaseResponse.success(dailyFocus));
    }

}
