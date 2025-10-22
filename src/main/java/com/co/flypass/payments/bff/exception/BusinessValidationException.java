package com.co.flypass.payments.bff.exception;

import org.springframework.http.HttpStatus;

import java.util.Objects;

public final class BusinessValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String code;
    private final HttpStatus httpStatus;

    public BusinessValidationException(String code, String message, HttpStatus status) {
        super(Objects.requireNonNull(message, "message must not be null").trim());
        final String c = Objects.requireNonNull(code, "code must not be null").trim();
        if (c.isEmpty()) throw new IllegalArgumentException("code must not be blank");
        this.code = c;
        this.httpStatus = status != null ? status : HttpStatus.BAD_REQUEST;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public static BusinessValidationException badRequest(String code, String message) {
        return new BusinessValidationException(code, message, HttpStatus.BAD_REQUEST);
    }

    public static BusinessValidationException unauthorized(String code, String message) {
        return new BusinessValidationException(code, message, HttpStatus.UNAUTHORIZED);
    }

    public static BusinessValidationException forbidden(String code, String message) {
        return new BusinessValidationException(code, message, HttpStatus.FORBIDDEN);
    }

    public static BusinessValidationException conflict(String code, String message) {
        return new BusinessValidationException(code, message, HttpStatus.CONFLICT);
    }

    public static BusinessValidationException unprocessable(String code, String message) {
        return new BusinessValidationException(code, message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
