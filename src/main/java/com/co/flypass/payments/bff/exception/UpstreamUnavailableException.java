package com.co.flypass.payments.bff.exception;

import java.util.Objects;

public final class UpstreamUnavailableException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_RETRY_AFTER_SECONDS = 30;

    private final int retryAfterSeconds;

    public UpstreamUnavailableException(int retryAfterSeconds, Throwable cause) {
        super("Payments provider unavailable", Objects.requireNonNull(cause, "cause must not be null"));
        this.retryAfterSeconds = Math.max(0, retryAfterSeconds);
    }

    public UpstreamUnavailableException(Throwable cause) {
        this(DEFAULT_RETRY_AFTER_SECONDS, cause);
    }

    public int getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
