package com.example.Balenz.user.service;

import com.example.Balenz.global.exception.BaseException;
import com.example.Balenz.global.exception.ErrorCode;
import com.example.Balenz.global.security.CookieUtil;
import com.example.Balenz.global.security.JwtProvider;
import com.example.Balenz.user.dto.TokenDto;
import com.example.Balenz.user.dto.TokenInfoDto;
import com.example.Balenz.user.entity.RefreshToken;
import com.example.Balenz.user.entity.User;
import com.example.Balenz.user.repository.RefreshTokenRepository;
import com.example.Balenz.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final CookieUtil cookieUtil;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public void reissue(HttpServletResponse response, String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BaseException(ErrorCode.INVALID_INPUT_VALUE, "refresh token이 null이거나 비어있습니다.");
        }

        Long userId;
        try {
            TokenInfoDto tokenInfo = jwtProvider.getInfoFromToken(refreshToken);
            userId = tokenInfo.getId();
        } catch (CredentialsExpiredException e) {
            throw new BaseException(ErrorCode.EXPIRED_TOKEN);
        } catch (BadCredentialsException e) {
            throw new BaseException(ErrorCode.INVALID_TOKEN);
        }

        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUser_Id(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.TOKEN_NOT_FOUND, "해당 사용자 id의 refresh token을 찾을 수 없습니다."));

        if (!refreshTokenEntity.getRefreshToken().equals(refreshToken))
            throw new BaseException(ErrorCode.INVALID_TOKEN, "refresh token 값이 올바르지 않습니다.");

        TokenDto tokens = createAndSaveToken(userId);
        cookieUtil.setTokenCookies(response, tokens.getAccessToken(), tokens.getRefreshToken());
    }

    @Transactional
    public TokenDto createAndSaveToken(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BaseException(ErrorCode.USER_NOT_FOUND, "해당 id의 사용자를 찾을 수 없습니다."));

        TokenDto tokens = jwtProvider.createTokens(userId, user.getEmail(), user.getRole());

        // refresh token rotate
        refreshTokenRepository.findByUser_Id(userId)
                .ifPresentOrElse(
                        rt -> rt.updateRefreshToken(tokens.getRefreshToken()),
                        () -> refreshTokenRepository.save(RefreshToken.builder()
                                .user(user).refreshToken(tokens.getRefreshToken()).build())
                );
        return tokens;
    }

}
