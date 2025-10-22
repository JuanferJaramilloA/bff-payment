package com.co.flypass.payments.bff.client;

import com.co.flypass.payments.bff.client.metadata.ProviderMetadata;
import com.co.flypass.payments.bff.model.PaymentMethodListItem;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

public interface ServiceClient {

    @NonNull
    List<PaymentMethodListItem> listPaymentMethods(@NonNull String walletId,
                                                   @Nullable String authorizationHeader);

    @NonNull
    ProviderMetadata getMetadata();

    default boolean isHealthy() {
        return true;
    }
}
