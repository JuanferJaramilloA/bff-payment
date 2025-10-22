package com.co.flypass.payments.bff.client.metadata;

public record ProviderMetadata(String providerId, String displayName) {

    public ProviderMetadata {
        if (providerId == null || providerId.trim().isEmpty()) {
            throw new IllegalArgumentException("providerId must not be blank");
        }
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new IllegalArgumentException("displayName must not be blank");
        }
        providerId = providerId.trim();
        displayName = displayName.trim();
    }

    public static ProviderMetadata of(String providerId, String displayName) {
        return new ProviderMetadata(providerId, displayName);
    }
}
