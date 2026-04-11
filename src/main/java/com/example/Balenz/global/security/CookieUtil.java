package com.example.Balenz.global.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    @Value("${COOKIE_SAMESITE}")
    private String COOKIE_SAMESITE;

    @Value("${COOKIE_SECURE}")
    private boolean COOKIE_SECURE;

    /** accessToken, refreshToken 세팅 */
    public void setTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        addCookie(response, "accessToken", accessToken,
                (int) (JwtProvider.EXPIRE_ACCESS / 1000), COOKIE_SECURE, COOKIE_SAMESITE);
        addCookie(response, "refreshToken", refreshToken,
                (int) (JwtProvider.EXPIRE_REFRESH / 1000), COOKIE_SECURE, COOKIE_SAMESITE);
    }

    /** 쿠키 세팅 */
    public void addCookie(HttpServletResponse response,
                          String name, String value,
                          int maxAgeSeconds, boolean secure, String sameSite) {
        String cookie = name + "=" + value
                + "; Max-Age=" + maxAgeSeconds
                + "; Path=/"
                + "; HttpOnly"
                + (secure ? "; Secure" : "")
                + "; SameSite=" + sameSite;

        response.addHeader("Set-Cookie", cookie);
    }

    /** 쿠키 삭제 */
    public void expireCookie(HttpServletResponse response,
                             String name,
                             boolean secure, String sameSite) {
        String cookie = name + "="
                + "; Max-Age=0"
                + "; Path=/"
                + "; HttpOnly"
                + (secure ? "; Secure" : "")
                + "; SameSite=" + sameSite;

        response.addHeader("Set-Cookie", cookie);
    }

}
