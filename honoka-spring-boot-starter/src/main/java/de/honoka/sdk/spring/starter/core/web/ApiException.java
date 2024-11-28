package de.honoka.sdk.spring.starter.core.web;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private boolean printStackTrace = false;

    public ApiException() {}

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, boolean printStackTrace) {
        super(message);
        this.printStackTrace = printStackTrace;
    }
}
