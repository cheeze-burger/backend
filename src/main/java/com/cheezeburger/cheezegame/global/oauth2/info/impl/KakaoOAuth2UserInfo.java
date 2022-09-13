package com.cheezeburger.cheezegame.global.oauth2.info.impl;

import com.cheezeburger.cheezegame.global.oauth2.info.OAuth2UserInfo;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

    private final Map<String, Object> kakaoAccountInfo;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
        kakaoAccountInfo = (Map<String, Object>) attributes.get("kakao_account");
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        return (boolean) kakaoAccountInfo.get("has_email") ? (String) kakaoAccountInfo.get("email") : null;
    }

    @Override
    public String getGender() {
        return (boolean) kakaoAccountInfo.get("has_gender") ? (String) kakaoAccountInfo.get("gender") : null;
    }

    @Override
    public String getAgeRange() {
        return (boolean) kakaoAccountInfo.get("has_age_range") ? (String) kakaoAccountInfo.get("age_range") : null;
    }
}
