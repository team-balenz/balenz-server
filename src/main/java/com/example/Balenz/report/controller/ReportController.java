package com.example.Balenz.report.controller;

import com.example.Balenz.global.response.BaseResponse;
import com.example.Balenz.global.security.CustomPrincipal;
import com.example.Balenz.report.dto.ReportSubmitRequestDto;
import com.example.Balenz.report.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<?> submitReport(@Valid @RequestBody ReportSubmitRequestDto requestDto,
                                          @AuthenticationPrincipal CustomPrincipal customPrincipal) {
        Long userId = customPrincipal != null ? customPrincipal.getId() : null;
        reportService.submitReport(requestDto, userId);
        return ResponseEntity.ok().body(BaseResponse.success(null));
    }

}
