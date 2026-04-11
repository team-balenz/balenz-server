package com.example.Balenz.global.security;

import com.example.Balenz.user.dto.TokenDto;
import com.example.Balenz.user.service.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${COOKIE_SECURE}")
    private boolean COOKIE_SECURE;

    @Value("${COOKIE_SAMESITE}")
    private String COOKIE_SAMESITE;

    @Value("${REDIRECT_URI}")
    private String REDIRECT_URI;

    private final CookieUtil cookieUtil;
    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 1. CustomPrincipal 추출
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();

        // 2. JWT 생성
        TokenDto tokens = tokenService.createAndSaveToken(principal.getId());

        // 3. HttpOnly 쿠키로 토큰 내려주기
        cookieUtil.setTokenCookies(response, tokens.getAccessToken(), tokens.getRefreshToken());

        // 4. OAuth2 로그인 과정에서 생긴 세션 무효화
        HttpSession session = request.getSession(false);
        if (session != null)
            session.invalidate();

        cookieUtil.expireCookie(response, "JSESSIONID", COOKIE_SECURE, COOKIE_SAMESITE);

        // 5. 프론트로 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, REDIRECT_URI);
    }

}
