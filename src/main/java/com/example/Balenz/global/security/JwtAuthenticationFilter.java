package com.example.Balenz.global.security;

import com.example.Balenz.global.exception.ErrorCode;
import com.example.Balenz.user.dto.TokenInfoDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);

        if (token != null) {
            try {
                // 토큰 파싱 + 유효성 체크
                TokenInfoDto tokenInfo = jwtProvider.getInfoFromToken(token);

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Principal 생성
                    CustomPrincipal principal = CustomPrincipal.fromJwt(tokenInfo.getId(),
                            tokenInfo.getEmail(), tokenInfo.getRole());

                    // Authentication 생성 후 Security Context에 세팅
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

            } catch (BadCredentialsException e) {
                SecurityContextHolder.clearContext();
                request.setAttribute("exceptionCode", ErrorCode.INVALID_TOKEN);
                request.setAttribute("exceptionMessage", e.getMessage());

            } catch (CredentialsExpiredException e) {
                SecurityContextHolder.clearContext();
                request.setAttribute("exceptionCode", ErrorCode.EXPIRED_TOKEN);
                request.setAttribute("exceptionMessage", e.getMessage());

            } catch (Exception e) {
                log.error("토큰 처리 예외" + e.getMessage());
                SecurityContextHolder.clearContext();
                request.setAttribute("exceptionCode", ErrorCode.INVALID_TOKEN);
                request.setAttribute("exceptionMessage", "토큰 처리 중 오류가 발생했습니다.");
            }
        }
        filterChain.doFilter(request, response);
    }

    /** 토큰 파싱 **/
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // Authorization 헤더가 없는 경우 Cookie에서 accessToken 찾기
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (var cookie : cookies) {
            if ("accessToken".equals(cookie.getName()))
                return cookie.getValue();
        }

        return null;
    }

}
