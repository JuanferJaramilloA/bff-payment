package com.co.flypass.payments.bff.client;

import com.co.flypass.payments.bff.model.PaymentMethodListItem;
import java.util.List;

public interface ServiceClient {
    List<PaymentMethodListItem> listPaymentMethods(String walletId, String authorizationHeader);
}
