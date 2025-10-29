package com.co.flypass.payments.bff.client;

import com.co.flypass.payments.bff.client.metadata.ProviderMetadata;
import com.co.flypass.payments.bff.model.LinkResultDTO;
import com.co.flypass.payments.bff.model.PaymentMethodListItem;
import com.co.flypass.payments.bff.model.RechargeInitResultDTO;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

public interface ServiceClient {

    @NonNull
    List<PaymentMethodListItem> listPaymentMethods(@NonNull String walletId,
                                                   @Nullable String authorizationHeader);

    default LinkResultDTO linkPaymentMethod(@NonNull String walletId,
                                            @Nullable String authorizationHeader,
                                            @Nullable JsonNode body) {
        throw new UnsupportedOperationException("linkPaymentMethod not supported");
    }

    default RechargeInitResultDTO rechargeInit(@NonNull String walletId,
                                               @Nullable String authorizationHeader,
                                               @Nullable JsonNode body) {
        throw new UnsupportedOperationException("rechargeInit not supported");
    }

    @NonNull
    ProviderMetadata getMetadata();

    default boolean isHealthy() {
        return true;
    }
}
