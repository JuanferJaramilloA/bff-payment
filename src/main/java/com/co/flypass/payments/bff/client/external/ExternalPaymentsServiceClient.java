package com.co.flypass.payments.bff.client.external;

import com.co.flypass.payments.bff.adapter.ExternalPaymentsAdapter;
import com.co.flypass.payments.bff.client.AbstractServiceClient;
import com.co.flypass.payments.bff.client.metadata.ProviderMetadata;
import com.co.flypass.payments.bff.client.external.dto.ExternalPaymentsApiResponse;
import com.co.flypass.payments.bff.config.PaymentsProperties;
import com.co.flypass.payments.bff.config.RestClientFactory;
import com.co.flypass.payments.bff.model.PaymentMethodListItem;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component("external")
public class ExternalPaymentsServiceClient extends AbstractServiceClient<ExternalPaymentsApiResponse> {

    private static final String PROVIDER_ID = "external";
    private static final String PAYMENT_METHODS_PATH = "/payment-methods";

    private final PaymentsProperties paymentsProperties;
    private final RestClientFactory restClientFactory;
    private final ExternalPaymentsAdapter adapter;

    private RestClient restClient;

    public ExternalPaymentsServiceClient(MeterRegistry meterRegistry,
                                         CircuitBreakerRegistry circuitBreakerRegistry,
                                         RetryRegistry retryRegistry,
                                         PaymentsProperties paymentsProperties,
                                         RestClientFactory restClientFactory,
                                         ExternalPaymentsAdapter adapter) {
        super(meterRegistry, circuitBreakerRegistry, retryRegistry);
        this.paymentsProperties = paymentsProperties;
        this.restClientFactory = restClientFactory;
        this.adapter = adapter;
    }

    @PostConstruct
    void init() {
        var serviceConfig = paymentsProperties.require(PROVIDER_ID);
        this.restClient = restClientFactory.buildRestClientFor(serviceConfig);
    }

    @Override
    protected ExternalPaymentsApiResponse fetchPaymentMethods(String walletId, String authorizationHeader) {
        RestClient.RequestHeadersSpec<?> req = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(PAYMENT_METHODS_PATH)
                        .queryParam("walletId", walletId)
                        .build());
        if (authorizationHeader != null && !authorizationHeader.isBlank()) {
            req = req.header(HttpHeaders.AUTHORIZATION, authorizationHeader);
        }
        return req.retrieve().body(ExternalPaymentsApiResponse.class);
    }

    @Override
    protected List<PaymentMethodListItem> adaptPaymentMethods(ExternalPaymentsApiResponse raw) {
        return adapter.adapt(raw);
    }

    @Override
    public ProviderMetadata getMetadata() {
        return ProviderMetadata.of(PROVIDER_ID, "ExternalPayments");
    }
}
