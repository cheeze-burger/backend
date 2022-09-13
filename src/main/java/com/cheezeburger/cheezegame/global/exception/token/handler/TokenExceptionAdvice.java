package com.cheezeburger.cheezegame.global.exception.token.handler;

import com.cheezeburger.cheezegame.global.dto.ErrorResponseDto;
import com.cheezeburger.cheezegame.global.exception.token.InvalidRefreshTokenException;
import com.cheezeburger.cheezegame.global.exception.token.NotExpiredTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class TokenExceptionAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotExpiredTokenException.class)
    public ErrorResponseDto<Map<String, String>> notExpiredTokenExceptionHandler(NotExpiredTokenException e) {
        Map<String, String> data = new HashMap<>();
        data.put("cause", "EXPIRED_ACCESS_TOKEN");
        data.put("message", e.getMessage());
        return ErrorResponseDto.<Map<String, String>>builder()
                .data(data)
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ErrorResponseDto<Map<String, String>> invalidRefreshTokenException(InvalidRefreshTokenException e) {
        Map<String, String> data = new HashMap<>();
        data.put("cause", "INVALID_REFRESH_TOKEN");
        data.put("message", e.getMessage());
        return ErrorResponseDto.<Map<String, String>>builder()
                .data(data)
                .build();
    }
}
