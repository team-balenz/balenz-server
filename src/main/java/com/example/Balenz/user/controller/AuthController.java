package com.example.Balenz.user.controller;

import com.example.Balenz.global.response.BaseResponse;
import com.example.Balenz.user.dto.LoginDto;
import com.example.Balenz.user.dto.SignUpDto;
import com.example.Balenz.user.service.AuthService;
import com.example.Balenz.user.service.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(HttpServletResponse response,
                                    @Valid @RequestBody SignUpDto signUpDto) {
        authService.signUp(response, signUpDto);
        return ResponseEntity.ok()
                .body(BaseResponse.success(null));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(HttpServletResponse response,
                                   @Valid @RequestBody LoginDto loginDto) {
        authService.login(response, loginDto);
        return ResponseEntity.ok()
                .body(BaseResponse.success(null));
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletResponse response,
                                     @CookieValue(value = "refreshToken", required = false) String refreshToken) {
        tokenService.reissue(response, refreshToken);
        return ResponseEntity.ok()
                .body(BaseResponse.success(null));
    }

}
