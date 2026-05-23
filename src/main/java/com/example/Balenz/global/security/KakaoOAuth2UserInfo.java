package com.example.Balenz.global.security;

import java.util.Map;

public class KakaoOAuth2UserInfo implements OAuth2UserInfo {

    /**
     * 카카오 응답 구조
     * {
     *   "id": ...,
     *   "kakao_account": {
     *     "email": "...",
     *     "profile": {
     *       "nickname": "...",
     *       "profile_image_url": "https://..."
     *     }
     *   }
     * }
     */
    private final Map<String, Object> attributes; // 응답 원본
    private final Map<String, Object> kakaoAccount;
    private final Map<String, Object> profile;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        this.profile = kakaoAccount == null ? null : (Map<String, Object>) kakaoAccount.get("profile");
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getProvider() { return "kakao"; }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getName() {
        return profile.get("nickname").toString();
    }

    @Override
    public String getImageUrl() {
        return profile.get("profile_image_url").toString();
    }

    @Override
    public String getEmail() {
        Object email = kakaoAccount.get("email");
        return email != null ? email.toString() : null;
    }
}
