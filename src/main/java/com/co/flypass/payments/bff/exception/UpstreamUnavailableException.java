package com.co.flypass.payments.bff.exception;

import lombok.Getter;

@Getter
public class UpstreamUnavailableException extends RuntimeException {
  private final int retryAfterSeconds;
  public UpstreamUnavailableException(int retryAfterSeconds, Throwable cause) {
    super("Payments provider unavailable", cause);
    this.retryAfterSeconds = retryAfterSeconds;
  }
}
