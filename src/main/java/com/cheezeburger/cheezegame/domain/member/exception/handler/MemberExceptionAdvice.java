package com.cheezeburger.cheezegame.domain.member.exception.handler;

import com.cheezeburger.cheezegame.domain.member.exception.MemberNotFoundException;
import com.cheezeburger.cheezegame.global.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = {"com.cheezeburger.oauth.domain.member"})
public class MemberExceptionAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MemberNotFoundException.class)
    public ErrorResponseDto<String> memberNotFoundExceptionHandler(MemberNotFoundException e) {
        return ErrorResponseDto.<String>builder()
                .data(e.getMessage())
                .build();
    }
}
