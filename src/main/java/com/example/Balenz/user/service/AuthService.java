package com.example.Balenz.user.service;

import com.example.Balenz.global.exception.BaseException;
import com.example.Balenz.global.exception.ErrorCode;
import com.example.Balenz.user.dto.LoginDto;
import com.example.Balenz.user.dto.SignUpDto;
import com.example.Balenz.user.dto.TokenDto;
import com.example.Balenz.user.entity.Role;
import com.example.Balenz.user.entity.User;
import com.example.Balenz.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(HttpServletResponse response, SignUpDto signUpDto) {
        String password1 = signUpDto.getPassword1();
        String password2 = signUpDto.getPassword2();
        String email = signUpDto.getEmail();

        if (!password1.equals(password2)) {
            throw new BaseException(ErrorCode.INVALID_INPUT_VALUE, "비밀번호가 일치하지 않습니다.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new BaseException(ErrorCode.DUPLICATED_EMAIL, "해당 이메일로 가입된 계정이 이미 존재합니다.");
        }

        String encodedPassword = passwordEncoder.encode(password1);

        User user = userRepository.save(User.builder()
                .nickname(signUpDto.getNickname())
                .email(email)
                .password(encodedPassword)
                .role(Role.ROLE_USER).build());

        TokenDto tokens = tokenService.createAndSaveToken(user.getId());
        tokenService.setCookie(response, tokens.getAccessToken(), tokens.getRefreshToken());
    }

    @Transactional
    public void login(HttpServletResponse response, LoginDto loginDto) {
        String email = loginDto.getEmail();
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new BaseException(ErrorCode.LOGIN_FAILED, "잘못된 이메일 혹은 비밀번호입니다."));

        String password = loginDto.getPassword();
        if (password == null || password.isBlank() || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BaseException(ErrorCode.LOGIN_FAILED, "잘못된 이메일 혹은 비밀번호입니다.");
        }

        TokenDto tokens = tokenService.createAndSaveToken(user.getId());
        tokenService.setCookie(response, tokens.getAccessToken(), tokens.getRefreshToken());
    }

}
