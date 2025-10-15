package com.co.flypass.payments.bff.client.bancolombia;

import com.co.flypass.payments.bff.client.ServiceClient;
import com.co.flypass.payments.bff.client.bancolombia.dto.BancolombiaPaymentModeApiResponse;
import com.co.flypass.payments.bff.config.PaymentsProperties;
import com.co.flypass.payments.bff.config.RestClientFactory;
import com.co.flypass.payments.bff.exception.UpstreamUnavailableException;
import com.co.flypass.payments.bff.mapper.PaymentMethodListItemMapper;
import com.co.flypass.payments.bff.model.PaymentMethodListItem;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component("bancolombia")
@RequiredArgsConstructor
public class BancolombiaServiceClient implements ServiceClient {

    private static final String SERVICE_ID = "bancolombia";
    private static final String PAYMENT_METHODS_PATH = "/user/paymentMode";

    private final PaymentsProperties paymentsProperties;
    private final RestClientFactory restClientFactory;
    private final PaymentMethodListItemMapper paymentMethodListItemMapper;

    private RestClient restClient;

    @PostConstruct
    void init() {
        PaymentsProperties.ServiceConfig serviceConfig = paymentsProperties.require(SERVICE_ID);
        this.restClient = restClientFactory.buildRestClientFor(serviceConfig);
    }

    @Override
    @Retry(name = "bancolombia-listPaymentMethods", fallbackMethod = "fallback")
    @CircuitBreaker(name = "bancolombia")
    public List<PaymentMethodListItem> listPaymentMethods(String walletId, String authorizationHeader) {
        BancolombiaPaymentModeApiResponse apiResponse = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(PAYMENT_METHODS_PATH)
                        .queryParam("noMatterStatusPaymentMethod", true)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .retrieve()
                .body(BancolombiaPaymentModeApiResponse.class);

        if (apiResponse == null) return List.of();
        return List.of(paymentMethodListItemMapper.toListItem(apiResponse));
    }

    @SuppressWarnings("unused")
    private List<PaymentMethodListItem> fallback(String walletId, String authorizationHeader, Throwable cause) {
        throw new UpstreamUnavailableException(30, (cause instanceof Exception) ? (Exception) cause : new Exception(cause));
    }
}
