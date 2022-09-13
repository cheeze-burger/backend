package com.cheezeburger.cheezegame.global.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorResponseDto<T> {

    private final T data;

    @Builder
    public ErrorResponseDto(T data) {
        this.data = data;
    }
}
