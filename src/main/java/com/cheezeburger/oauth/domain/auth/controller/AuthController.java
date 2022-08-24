package com.cheezeburger.oauth.domain.auth.controller;

import com.cheezeburger.oauth.domain.member.entity.RefreshToken;
import com.cheezeburger.oauth.domain.member.repository.RefreshTokenRepository;
import com.cheezeburger.oauth.global.config.properties.AppProperties;
import com.cheezeburger.oauth.global.dto.BasicResponseDto;
import com.cheezeburger.oauth.global.exception.token.InvalidRefreshTokenException;
import com.cheezeburger.oauth.global.exception.token.NotExpiredTokenException;
import com.cheezeburger.oauth.global.oauth2.entity.Role;
import com.cheezeburger.oauth.global.oauth2.token.AuthToken;
import com.cheezeburger.oauth.global.oauth2.token.AuthTokenProvider;
import com.cheezeburger.oauth.global.utils.CookieUtil;
import com.cheezeburger.oauth.global.utils.HeaderUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AppProperties appProperties;

    private final AuthTokenProvider tokenProvider;

    private final RefreshTokenRepository refreshTokenRepository;

    private static final long THREE_DAYS_MSEC = 259200000;

    private static final String REFRESH_TOKEN = "refresh_token";

    @GetMapping("/refresh")
    public ResponseEntity<BasicResponseDto<Map<String, String>>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // access token 확인
        String accessToken = HeaderUtil.getAccessToken(request);
        AuthToken authToken = tokenProvider.convertAuthToken(accessToken);

        // expired access token 인지 확인
        Claims claims = authToken.getExpiredTokenClaims();
        if (claims == null) {
            throw new NotExpiredTokenException("토큰이 만료되지 않았습니다.");
        }

        Long memberSeq = claims.get("memberSeq", Long.class);
        Role role = Role.of(claims.get("role", String.class));

        // refresh token
        String refreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
                .map(Cookie::getValue)
                .orElse((null));
        AuthToken authRefreshToken = tokenProvider.convertAuthToken(refreshToken);

        if (!authRefreshToken.validate()) {
            throw new InvalidRefreshTokenException("리프레시 토큰이 유효하지 않습니다.");
        }

        // memberSeq refresh token 으로 DB 확인
        RefreshToken memberRefreshToken = refreshTokenRepository.findByMemberSeqAndToken(memberSeq, refreshToken)
                .orElseThrow(() -> new InvalidRefreshTokenException("리프레시 토큰이 유효하지 않습니다."));

        Date now = new Date();
        AuthToken newAccessToken = tokenProvider.createAuthToken(
                memberSeq,
                role.getAuthority(),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        // refresh 토큰 기간이 3일 이하로 남은 경우, refresh 토큰 갱신
        long validTime = authRefreshToken.getTokenClaims().getExpiration().getTime() - now.getTime();
        if (validTime <= THREE_DAYS_MSEC) {
            // refresh 토큰 설정
            long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

            authRefreshToken = tokenProvider.createAuthToken(
                    memberSeq,
                    new Date(now.getTime() + refreshTokenExpiry)
            );

            // DB에 refresh 토큰 업데이트
            memberRefreshToken.changeToken(authRefreshToken.getToken());
            refreshTokenRepository.saveAndFlush(memberRefreshToken);

            int cookieMaxAge = (int) refreshTokenExpiry / 60;
            CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
            CookieUtil.addCookie(response, REFRESH_TOKEN, authRefreshToken.getToken(), cookieMaxAge);
        }

        Map<String, String> data = new HashMap<>();
        data.put("token", newAccessToken.getToken());

        return new ResponseEntity<>(BasicResponseDto.<Map<String, String>>builder()
                .data(data)
                .build(), HttpStatus.OK);
    }
}
