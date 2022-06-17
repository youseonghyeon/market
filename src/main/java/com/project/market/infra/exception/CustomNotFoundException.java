package com.project.market.infra.exception;

/**
 * Custom 404 에러
 */
public class CustomNotFoundException extends RuntimeException {

    public CustomNotFoundException() {
        super();
    }

    public CustomNotFoundException(String message) {
        super(message);
    }

    public CustomNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomNotFoundException(Throwable cause) {
        super(cause);
    }
}
