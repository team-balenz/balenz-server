package com.example.Balenz.global.security;

import com.example.Balenz.global.exception.BaseException;
import com.example.Balenz.global.exception.ErrorCode;

import java.util.Map;

public class NaverOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;
    private final Map<String, Object> response;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        // 네이버는 사용자 정보가 response 안에 들어있음
        Object responseObj = attributes.get("response");
        if (!(responseObj instanceof Map))
            throw new BaseException(ErrorCode.INVALID_SOCIAL_PROVIDER, "네이버 OAuth 응답에 response가 없습니다.");
        this.response = (Map<String, Object>) responseObj;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        Object id = response.get("id");
        if (id == null)
            throw new BaseException(ErrorCode.INVALID_SOCIAL_PROVIDER, "네이버 OAuth response에 id가 없습니다.");
        return id.toString();
    }

    @Override
    public String getName() {
        Object nickname = response.get("nickname");
        if (nickname != null && !nickname.toString().isBlank())
            return nickname.toString();

        Object name = response.get("name");
        return name == null ? null : name.toString();
    }

    @Override
    public String getImageUrl() {
        Object profileImage = response.get("profile_image");
        if (profileImage != null && !profileImage.toString().isBlank())
            return profileImage.toString();
        return null;
    }

    @Override
    public String getEmail() {
        Object email = response.get("email");
        if (email != null && !email.toString().isBlank())
            return email.toString();
        return null;
    }

}
