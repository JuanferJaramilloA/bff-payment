package com.co.flypass.payments.bff.client;

import com.co.flypass.payments.bff.config.PaymentsProperties;
import com.co.flypass.payments.bff.exception.UnknownConnectorException;
import com.co.flypass.payments.bff.router.ServiceId;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ServiceClientRegistry {

    private final Map<String, ServiceClient> serviceClientsById;
    private final PaymentsProperties paymentsProperties;

    public ServiceClientRegistry(Map<String, ServiceClient> serviceClientsById,
                                 PaymentsProperties paymentsProperties) {
        this.serviceClientsById = serviceClientsById;
        this.paymentsProperties = paymentsProperties;
    }

    public ServiceClient get(ServiceId serviceId) {
        if (serviceId == null) {
            throw new UnknownConnectorException("(empty)", serviceClientsById.keySet());
        }
        String serviceKey = serviceId.value();

        boolean beanExists = serviceClientsById.containsKey(serviceKey);
        boolean configured = paymentsProperties.getServicesById() != null
                && paymentsProperties.getServicesById().containsKey(serviceKey);

        if (!(beanExists && configured)) {
            throw new UnknownConnectorException(serviceKey, serviceClientsById.keySet());
        }
        return serviceClientsById.get(serviceKey);
    }
}
