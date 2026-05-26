package com.example.Balenz.global.security;

import com.example.Balenz.global.exception.BaseException;
import com.example.Balenz.global.exception.ErrorCode;
import com.example.Balenz.user.entity.Provider;
import com.example.Balenz.user.entity.Role;
import com.example.Balenz.user.entity.SocialAccount;
import com.example.Balenz.user.entity.User;
import com.example.Balenz.user.repository.SocialAccountRepository;
import com.example.Balenz.user.repository.UserRepository;
import com.example.Balenz.user.service.NicknameService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Provider로부터 받은 정보를 User로 매핑
 * -> CustomPrincipal로 반환
 * */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final SocialAccountRepository socialAccountRepository;
    private final UserRepository userRepository;
    private final NicknameService nicknameService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. provider에서 사용자 정보 (attributes) 조회
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2. 어떤 provider인지 확인 (application.yml의 registrationId)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 3. provider별 OAuth2UserInfo 객체 생성
        OAuth2UserInfo userInfo = getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        Provider provider = Provider.valueOf(userInfo.getProvider().toUpperCase());
        String providerId = userInfo.getProviderId();

        // 4. 해당 소셜 계정으로 이미 가입되어있는 경우 기존 user 가져오기, 없을 경우 생성
        User user;
        try {
            user = socialAccountRepository.findByProviderAndProviderUserId(provider, providerId)
                    .map(SocialAccount::getUser)
                    .orElseGet(() -> createUserWithSocialAccount(provider, providerId, userInfo));
        } catch (BaseException e) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error(e.getErrorCode().name()),
                    e);
        }

        return CustomPrincipal
                .fromOAuth(user.getId(), user.getEmail(), user.getRole(), oAuth2User.getAttributes());
    }

    /** registrationId에 맞는 OAuth2UserInfo 구현체 생성 */
    private OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "naver" -> new NaverOAuth2UserInfo(attributes);
            case "kakao" -> new KakaoOAuth2UserInfo(attributes);
            default ->
                    throw new BaseException(ErrorCode.INVALID_SOCIAL_PROVIDER, "지원하지 않는 소셜 로그인입니다. - " + registrationId);
        };
    }

    /** 신규 user 생성 + 소셜 계정 연결 */
    private User createUserWithSocialAccount(Provider provider,
                                            String providerUserId,
                                            OAuth2UserInfo userInfo) {
        String name = nicknameService.generateRandomNickname();

        String email = userInfo.getEmail();
        if (email == null || email.isBlank()) {
            throw new BaseException(ErrorCode.EMAIL_REQUIRED, provider + "에서 이메일을 제공받지 못했습니다.");
        }

        // 이미 해당 이메일로 가입한 유저가 있는 경우 에러
        if (userRepository.existsByEmail(email)) {
            socialAccountRepository.findByUser_Email(email)
                    .ifPresentOrElse(
                            // 소셜 계정으로 가입했던 경우
                            socialAccount -> {
                                Provider socialAccountProvider = socialAccount.getProvider();
                                throw new BaseException(ErrorCode.SOCIAL_ACCOUNT_ALREADY_EXISTS,
                                        socialAccountProvider.name());
                            },
                            // 일반 계정으로 가입했던 경우
                            () -> {
                                throw new BaseException(ErrorCode.LOCAL_ACCOUNT_ALREADY_EXISTS, "이미 일반 계정으로 가입된 이메일입니다. 일반 로그인을 진행해주세요.");
                            }
                    );
        }

        User user = userRepository.save(
                User.builder()
                        .nickname(name)
                        .email(email)
                        .role(Role.ROLE_USER).build());

        SocialAccount socialAccount = SocialAccount.builder()
                .provider(provider)
                .providerUserId(providerUserId).build();

        user.linkSocialAccount(socialAccount);
        socialAccountRepository.save(socialAccount);

        return user;
    }

}
