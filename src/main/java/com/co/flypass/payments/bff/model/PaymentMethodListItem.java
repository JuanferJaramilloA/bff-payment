package com.co.flypass.payments.bff.model;

public record PaymentMethodListItem(
        String brand,
        String brandIconUrl,
        String productLabel,
        boolean isDefault,
        String holderName,
        String maskedNumber
) {}
