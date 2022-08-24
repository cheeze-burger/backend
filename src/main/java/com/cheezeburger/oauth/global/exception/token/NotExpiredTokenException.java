package com.cheezeburger.oauth.global.exception.token;

public class NotExpiredTokenException extends RuntimeException {

    public NotExpiredTokenException(String message) {
        super(message);
    }
}
