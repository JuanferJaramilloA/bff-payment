package com.co.flypass.payments.bff.adapter;

import com.co.flypass.payments.bff.client.external.dto.ExternalPaymentsApiResponse;
import com.co.flypass.payments.bff.model.PaymentMethodListItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public final class ExternalPaymentsAdapter {

    public List<PaymentMethodListItem> adapt(ExternalPaymentsApiResponse src) {
        if (src == null || src.items() == null || src.items().isEmpty()) {
            return List.of();
        }
        return src.items().stream()
                .filter(Objects::nonNull)
                .map(ExternalPaymentsAdapter::mapItem)
                .toList();
    }

    private static PaymentMethodListItem mapItem(ExternalPaymentsApiResponse.Item item) {
        final String brand        = NormalizationUtils.normalizeBrand(item.brand());
        final String masked       = NormalizationUtils.maskCardNumber(item.masked());
        final String productLabel = NormalizationUtils.normalizeProductLabel(item.productLabel(), "");
        final String holderName   = NormalizationUtils.defaultIfBlank(item.holderName(), "Titular");
        final String brandIconUrl = NormalizationUtils.defaultIfBlank(item.brandIconUrl(), "");

        return new PaymentMethodListItem(
                brand,
                brandIconUrl,
                productLabel,
                item.isDefault(),
                holderName,
                masked
        );
    }
}
