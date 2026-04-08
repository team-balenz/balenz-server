package com.example.Balenz.global.security;

import com.example.Balenz.user.dto.TokenDto;
import com.example.Balenz.user.dto.TokenInfoDto;
import com.example.Balenz.user.entity.Role;
import com.example.Balenz.user.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    public static final long EXPIRE_ACCESS = 1000 * 60 * 10; // 10분
    public static final long EXPIRE_REFRESH = 1000 * 60 * 60 * 24 * 7; // 1주
    private SecretKey secretKey;

    @Value("${JWT_SECRET}")
    private String secret;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** 토큰 (userId + email + role 포함) 생성 */
    public TokenDto createTokens(Long id, String email, Role role) {
        Date now = new Date();

        String accessToken = createToken(id, email, role, now, EXPIRE_ACCESS);
        String refreshToken = createToken(id, email, role, now, EXPIRE_REFRESH);

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken).build();
    }

    private String createToken(Long id, String email, Role role, Date now, long expireTime) {
        return Jwts.builder().setSubject(String.valueOf(id))
                .claim("role", role.name())
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expireTime)).
                signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(secretKey).build().
                    parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new CredentialsExpiredException("만료된 토큰입니다.");
        } catch (Exception e) {
            throw new BadCredentialsException("유효하지 않은 토큰입니다.");
        }
    }

    /** subject & claim 파싱 */
    public TokenInfoDto getInfoFromToken(String token) {
        Claims claims = parseClaims(token);

        String subject = claims.getSubject();
        Object roleObj = claims.get("role");
        Object emailObj = claims.get("email");

        if (subject == null || roleObj == null || emailObj == null) {
            throw new BadCredentialsException("토큰에 누락된 정보가 있습니다.");
        }

        try {
            return TokenInfoDto.builder()
                    .id(Long.valueOf(subject))
                    .email(emailObj.toString())
                    .role(Role.valueOf(roleObj.toString())).build();
        } catch (NumberFormatException e) {
            throw new BadCredentialsException("토큰의 subject 값이 숫자가 아닙니다.");
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("토큰값이 올바르지 않습니다.");
        }
    }

}