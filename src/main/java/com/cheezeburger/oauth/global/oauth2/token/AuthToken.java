package com.cheezeburger.oauth.global.oauth2.token;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SecurityException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
public class AuthToken {

    @Getter
    private final String token;


    private final Key key;

    private static final String AUTHORITIES_KEY = "role";

    AuthToken(Long memberSeq, Date expiry, Key key) {
        this.key = key;
        this.token = createAuthToken(memberSeq, expiry);
    }

    AuthToken(Long memberSeq, String email, String role, Date expiry, Key key) {
        this.key = key;
        this.token = createAuthToken(memberSeq, email, role, expiry);
    }

    private String createAuthToken(Long memberSeq, String email, String role, Date expiry) {
        return Jwts.builder()
                .claim("memberSeq", memberSeq)
                .claim("email", email)
                .claim(AUTHORITIES_KEY, role)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expiry)
                .compact();
    }

    private String createAuthToken(Long memberSeq, Date expiry) {
        return Jwts.builder()
                .claim("memberSeq", memberSeq)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expiry)
                .compact();
    }

    public boolean validate() {
        return this.getTokenClaims() != null;
    }

    public Claims getTokenClaims() {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SecurityException e) {
            log.info("Invalid JWT Signature.");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
        }

        return null;
    }

    public Claims getExpiredTokenClaims() {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            return e.getClaims();
        }

        return null;
    }
}
