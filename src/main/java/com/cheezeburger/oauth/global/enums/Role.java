package com.cheezeburger.oauth.global.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    MEMBER("ROLE_MEMBER", "ordinary member");

    private final String authority;

    private final String description;

    Role(String authority, String description) {
        this.authority = authority;
        this.description = description;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public String getDescription() {
        return description;
    }
}
