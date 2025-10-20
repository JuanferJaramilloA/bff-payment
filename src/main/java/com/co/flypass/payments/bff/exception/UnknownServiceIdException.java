package com.co.flypass.payments.bff.exception;

import lombok.Getter;

import java.util.Set;

@Getter
public final class UnknownServiceIdException extends RuntimeException {
    private final String serviceId;
    private final Set<String> allowed;

    public UnknownServiceIdException(String serviceId, Set<String> allowed) {
        super("Unknown serviceId: " + serviceId);
        this.serviceId = serviceId;
        this.allowed = allowed;
    }
}
