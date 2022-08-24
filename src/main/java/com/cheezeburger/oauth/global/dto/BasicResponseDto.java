package com.cheezeburger.oauth.global.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BasicResponseDto<T> {

    private final T data;

    @Builder
    public BasicResponseDto(T data) {
        this.data = data;
    }
}
