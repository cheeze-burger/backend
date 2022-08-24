package com.cheezeburger.oauth.global.oauth2.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Role implements GrantedAuthority {

    MEMBER("ROLE_MEMBER", "일반 회원"),
    ADMIN("ROLE_ADMIN", "관리자"),
    GUEST("GUEST", "게스트");

    private final String authority;

    private final String displayName;

    public static Role of(String authority) {
        return Arrays.stream(Role.values())
                .filter(r -> r.getAuthority().equals(authority))
                .findAny()
                .orElse(GUEST);
    }


    @Override
    public String getAuthority() {
        return authority;
    }
}
