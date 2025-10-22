package com.co.flypass.payments.bff.client.external.dto;

import java.util.List;

public record ExternalPaymentsApiResponse(List<Item> items) {
    public record Item(
            String brand,
            String masked,
            String productLabel,
            boolean isDefault,
            String holderName,
            String brandIconUrl
    ) {}
}
