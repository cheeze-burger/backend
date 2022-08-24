package com.cheezeburger.oauth.global.oauth2.info;

import com.cheezeburger.oauth.global.oauth2.entity.ProviderType;
import com.cheezeburger.oauth.global.oauth2.info.impl.KakaoOAuth2UserInfo;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(ProviderType providerType, Map<String, Object> attributes) {
        switch (providerType) {
            case KAKAO:
                return new KakaoOAuth2UserInfo(attributes);
            default:
                throw new IllegalArgumentException("Invalid Provider Type.");
        }
    }
}
