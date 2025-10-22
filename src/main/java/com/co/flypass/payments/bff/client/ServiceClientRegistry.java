package com.co.flypass.payments.bff.client;

import com.co.flypass.payments.bff.config.PaymentsProperties;
import com.co.flypass.payments.bff.exception.UnknownServiceIdException;
import com.co.flypass.payments.bff.router.ServiceId;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
public final class ServiceClientRegistry {

    private final Map<String, ServiceClient> serviceClientsById;
    private final PaymentsProperties paymentsProperties;

    public ServiceClientRegistry(@NonNull Map<String, ServiceClient> serviceClientsById,
                                 @NonNull PaymentsProperties paymentsProperties) {
        this.serviceClientsById = Objects.requireNonNull(serviceClientsById, "serviceClientsById must not be null");
        this.paymentsProperties = Objects.requireNonNull(paymentsProperties, "paymentsProperties must not be null");
    }

    @NonNull
    public ServiceClient get(@NonNull ServiceId serviceId) {
        final String key = Objects.requireNonNull(serviceId, "serviceId must not be null").value();

        final boolean beanExists = serviceClientsById.containsKey(key);
        final boolean configured = paymentsProperties.getServicesById() != null
                && paymentsProperties.getServicesById().containsKey(key);

        if (!beanExists || !configured) {
            throw new UnknownServiceIdException(key, serviceClientsById.keySet());
        }
        return serviceClientsById.get(key);
    }
}
