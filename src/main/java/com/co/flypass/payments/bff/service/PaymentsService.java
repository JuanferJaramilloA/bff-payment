package com.co.flypass.payments.bff.service;

import com.co.flypass.payments.bff.client.ServiceClient;
import com.co.flypass.payments.bff.client.ServiceClientRegistry;
import com.co.flypass.payments.bff.config.PaymentsProperties;
import com.co.flypass.payments.bff.model.PaymentMethodListItem;
import com.co.flypass.payments.bff.router.RouteContext;
import com.co.flypass.payments.bff.router.ServiceId;
import com.co.flypass.payments.bff.router.resolve.RouteResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentsService {

    private final RouteResolver routeResolver;
    private final ServiceClientRegistry serviceClientRegistry;
    private final PaymentsProperties paymentsProperties;

    public List<PaymentMethodListItem> getPaymentMethods(
            String walletId,
            String authorizationHeader,
            String serviceIdFromQuery,
            String serviceIdFromHeader
    ) {
        RouteContext routeContext = new RouteContext(
                serviceIdFromQuery,
                serviceIdFromHeader,
                paymentsProperties.getDefaultServiceId()
        );

        ServiceId selectedServiceId = routeResolver.resolve(routeContext);
        ServiceClient serviceClient = serviceClientRegistry.get(selectedServiceId);

        return serviceClient.listPaymentMethods(walletId, authorizationHeader);
    }
}
