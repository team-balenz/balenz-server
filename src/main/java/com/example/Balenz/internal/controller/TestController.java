package com.example.Balenz.internal.controller;

import com.example.Balenz.global.response.BaseResponse;
import com.example.Balenz.internal.dto.TestResponseDto;
import com.example.Balenz.internal.service.TestArticleService;
import com.example.Balenz.internal.service.TestService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(
        name = "Prompt Test",
        description = "프롬프트 테스트용 API"
)
public class TestController {

    private final TestService testService;
    private final TestArticleService testArticleService;

    @GetMapping("/test")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 실행"),
            @ApiResponse(responseCode = "404", description = "전달받은 쿼리파라미터 (id)에 해당하는 테스트 기사가 존재하지 않는 경우 (ARTICLE_NOT_FOUND)"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> testPrompt(@RequestParam("id") Long id) {
        TestResponseDto testResponseDto = testService.testPrompt(id);
        return ResponseEntity.ok(BaseResponse.success(testResponseDto));
    }

    @PostMapping("/admin/test-data")
    public ResponseEntity<?> saveTestArticleData() {
        int savedArticleCount = testArticleService.saveTestArticleData();
        return ResponseEntity.ok(BaseResponse.success(savedArticleCount));
    }

}
