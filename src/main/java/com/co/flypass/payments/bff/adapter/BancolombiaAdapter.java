package com.co.flypass.payments.bff.adapter;

import com.co.flypass.payments.bff.client.bancolombia.dto.BancolombiaPaymentModeApiResponse;
import com.co.flypass.payments.bff.mapper.PaymentMethodListItemMapper;
import com.co.flypass.payments.bff.model.PaymentMethodListItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class BancolombiaAdapter {

    private final PaymentMethodListItemMapper mapper;

    public BancolombiaAdapter(PaymentMethodListItemMapper mapper) {
        this.mapper = mapper;
    }

    public List<PaymentMethodListItem> adapt(BancolombiaPaymentModeApiResponse src) {
        if (src == null) return List.of();

        final PaymentMethodListItem mapped = mapper.toListItem(src);
        if (mapped == null) return List.of();

        return List.of(normalize(mapped));
    }

    private static PaymentMethodListItem normalize(PaymentMethodListItem item) {
        final String brand  = NormalizationUtils.normalizeBrand(item.brand());
        final String masked = NormalizationUtils.maskCardNumber(item.maskedNumber());

        return new PaymentMethodListItem(
                brand,
                item.brandIconUrl(),
                NormalizationUtils.normalizeProductLabel(item.productLabel(), ""),
                item.isDefault(),
                NormalizationUtils.defaultIfBlank(item.holderName(), ""),
                masked
        );
    }
}
