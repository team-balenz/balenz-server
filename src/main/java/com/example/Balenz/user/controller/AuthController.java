package com.example.Balenz.user.controller;

import com.example.Balenz.global.response.BaseResponse;
import com.example.Balenz.user.dto.SignUpDto;
import com.example.Balenz.user.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(HttpServletResponse response,
                                    @Valid @RequestBody SignUpDto signUpDto) {
        authService.signUp(response, signUpDto);
        return ResponseEntity.ok()
                .body(BaseResponse.success(null));
    }

}
