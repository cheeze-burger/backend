package com.cheezeburger.oauth.global.oauth2.service;

import com.cheezeburger.oauth.domain.member.entity.Member;
import com.cheezeburger.oauth.domain.member.repository.MemberRepository;
import com.cheezeburger.oauth.global.oauth2.entity.MemberPrincipal;
import com.cheezeburger.oauth.global.oauth2.entity.ProviderType;
import com.cheezeburger.oauth.global.oauth2.entity.Role;
import com.cheezeburger.oauth.global.oauth2.exception.OAuthProviderMissMatchException;
import com.cheezeburger.oauth.global.oauth2.info.OAuth2UserInfo;
import com.cheezeburger.oauth.global.oauth2.info.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        try {
            return this.proccess(userRequest, user);
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User proccess(OAuth2UserRequest userRequest, OAuth2User user) {
        ProviderType providerType = ProviderType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());
        Member savedMember = memberRepository.findByProviderId(userInfo.getProviderId())
                .orElse(null);

        if (savedMember != null) {
            if (providerType != savedMember.getProviderType()) {
                throw new OAuthProviderMissMatchException(
                        "Looks like you're signed up  with " + providerType +
                                " account. Pleas use your " + savedMember.getProviderType() +
                                " account to login."
                );
            }
        } else {
            savedMember = createMember(userInfo, providerType);
        }

        return MemberPrincipal.create(savedMember, user.getAttributes());
    }

    private Member createMember(OAuth2UserInfo userInfo, ProviderType providerType) {
        return memberRepository.saveAndFlush(Member.builder()
                .email(userInfo.getEmail())
                .password("NO_PASS")
                .gender(userInfo.getGender() == null ? null : Member.Gender.valueOf(userInfo.getGender().toUpperCase()))
                .ageRange(userInfo.getAgeRange())
                .role(Role.MEMBER)
                .providerType(providerType)
                .providerId(userInfo.getProviderId())
                .build());
    }
}
