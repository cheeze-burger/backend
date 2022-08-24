package com.cheezeburger.oauth.global.oauth2.handler;

import com.cheezeburger.oauth.domain.member.entity.Member;
import com.cheezeburger.oauth.domain.member.entity.RefreshToken;
import com.cheezeburger.oauth.domain.member.exception.MemberNotFoundException;
import com.cheezeburger.oauth.domain.member.repository.MemberRepository;
import com.cheezeburger.oauth.domain.member.repository.RefreshTokenRepository;
import com.cheezeburger.oauth.global.config.properties.AppProperties;
import com.cheezeburger.oauth.global.oauth2.entity.ProviderType;
import com.cheezeburger.oauth.global.oauth2.entity.Role;
import com.cheezeburger.oauth.global.oauth2.info.OAuth2UserInfo;
import com.cheezeburger.oauth.global.oauth2.info.OAuth2UserInfoFactory;
import com.cheezeburger.oauth.global.oauth2.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.cheezeburger.oauth.global.oauth2.token.AuthToken;
import com.cheezeburger.oauth.global.oauth2.token.AuthTokenProvider;
import com.cheezeburger.oauth.global.utils.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import static com.cheezeburger.oauth.global.oauth2.repository.OAuth2AuthorizationRequestBasedOnCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME;
import static com.cheezeburger.oauth.global.oauth2.repository.OAuth2AuthorizationRequestBasedOnCookieRepository.REFRESH_TOKEN;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthTokenProvider tokenProvider;

    private final AppProperties appProperties;

    private final MemberRepository memberRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new IllegalArgumentException("Sorry! We've got Unauthorized Redirect URI and can't proceed with the authentication");
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        ProviderType providerType = ProviderType.valueOf(authToken.getAuthorizedClientRegistrationId().toUpperCase());

        OidcUser user = (OidcUser) authentication.getPrincipal();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());

        Member member = memberRepository.findByEmail(userInfo.getEmail())
                .orElseThrow(() -> new MemberNotFoundException("회원 정보를 찾지 못했습니다."));


        Date now = new Date();
        AuthToken accessToken = tokenProvider.createAuthToken(
                member.getSeq(),
                member.getEmail(),
                getRole(user.getAuthorities()),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        // refresh 토큰 설정
        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

        AuthToken refreshToken = tokenProvider.createAuthToken(
                member.getSeq(),
                new Date(now.getTime() + refreshTokenExpiry)
        );

        // DB 저장
        RefreshToken memberRefreshToken = member.getRefreshToken();
        if (memberRefreshToken != null) {
            memberRefreshToken.changeToken(refreshToken.getToken());
            refreshTokenRepository.saveAndFlush(memberRefreshToken);
        } else {
            memberRefreshToken = RefreshToken.builder()
                    .member(member)
                    .token(refreshToken.getToken())
                    .build();
            refreshTokenRepository.saveAndFlush(memberRefreshToken);
        }

        int cookieMaxAge = (int) refreshTokenExpiry / 60;

        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", accessToken.getToken())
                .build().toUriString();
    }

    private String getRole(Collection<? extends GrantedAuthority> authorities) {
        for (Role role : Role.values()) {
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals(role.getAuthority())) {
                    return role.getAuthority();
                }
            }
        }

        return null;
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean hasAuthority(Collection<? extends GrantedAuthority> authorities, String authority) {
        if (authorities == null) {
            return false;
        }

        for (GrantedAuthority grantedAuthority : authorities) {
            if (authority.equals(grantedAuthority.getAuthority())) {
                return true;
            }
        }

        return false;
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return appProperties.getOauth2().getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    if (authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort()) {
                        return true;
                    }

                    return false;
                });
    }
}
