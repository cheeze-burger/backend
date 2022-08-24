package com.cheezeburger.oauth.domain.member.exception;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(String message) {
        super(message);
    }
}
