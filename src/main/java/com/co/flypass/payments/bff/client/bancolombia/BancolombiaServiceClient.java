package com.co.flypass.payments.bff.client.bancolombia;

import com.co.flypass.payments.bff.adapter.BancolombiaAdapter;
import com.co.flypass.payments.bff.client.AbstractServiceClient;
import com.co.flypass.payments.bff.client.bancolombia.dto.BancolombiaPaymentModeApiResponse;
import com.co.flypass.payments.bff.client.metadata.ProviderMetadata;
import com.co.flypass.payments.bff.config.PaymentsProperties;
import com.co.flypass.payments.bff.config.RestClientFactory;
import com.co.flypass.payments.bff.model.LinkResultDTO;
import com.co.flypass.payments.bff.model.PaymentMethodListItem;
import com.co.flypass.payments.bff.model.RechargeInitResultDTO;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component("bancolombia")
public final class BancolombiaServiceClient extends AbstractServiceClient<BancolombiaPaymentModeApiResponse> {

    private static final String PROVIDER_ID = "bancolombia";
    private static final String PAYMENT_METHODS_PATH = "/paymentMethod";
    private static final String RECHARGE_INIT_PATH = "/recharges/init";

    private final PaymentsProperties paymentsProperties;
    private final RestClientFactory restClientFactory;
    private final BancolombiaAdapter adapter;

    private RestClient restClient;

    public BancolombiaServiceClient(MeterRegistry meterRegistry,
                                    CircuitBreakerRegistry circuitBreakerRegistry,
                                    RetryRegistry retryRegistry,
                                    PaymentsProperties paymentsProperties,
                                    RestClientFactory restClientFactory,
                                    BancolombiaAdapter adapter) {
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
    protected BancolombiaPaymentModeApiResponse fetchPaymentMethods(String walletId, String authorizationHeader) {
        var req = restClient.get()
                .uri(b -> b.path(PAYMENT_METHODS_PATH)
                        .queryParam("noMatterStatusPaymentMethod", true)
                        .build());

        if (authorizationHeader != null && !authorizationHeader.isBlank()) {
            req = req.header(HttpHeaders.AUTHORIZATION, authorizationHeader);
        }
        String corr = MDC.get("correlationId");
        if (corr != null && !corr.isBlank()) {
            req = req.header("X-Correlation-Id", corr);
        }

        return req.retrieve().body(BancolombiaPaymentModeApiResponse.class);
    }

    @Override
    protected List<PaymentMethodListItem> adaptPaymentMethods(BancolombiaPaymentModeApiResponse raw) {
        return adapter.adapt(raw);
    }

    @Override
    protected JsonNode fetchLinkCall(String walletId, String authorizationHeader, JsonNode body) {
        var req = restClient.post()
                .uri(PAYMENT_METHODS_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
        if (authorizationHeader != null && !authorizationHeader.isBlank()) {
            req = req.header(HttpHeaders.AUTHORIZATION, authorizationHeader);
        }
        String corr = MDC.get("correlationId");
        if (corr != null && !corr.isBlank()) {
            req = req.header("X-Correlation-Id", corr);
        }
        return req.retrieve().body(JsonNode.class);
    }

    @Override
    protected LinkResultDTO adaptLinkResponse(JsonNode raw) {
        if (raw == null || raw.isNull()) return new LinkResultDTO(null, null, null, null, null);
        Boolean requiresOtp = raw.has("requiresOtp") ? raw.get("requiresOtp").asBoolean() : null;
        Boolean requiresSignature = raw.has("requiresSignature") ? raw.get("requiresSignature").asBoolean() : null;
        String status = raw.path("status").asText(null);
        String providerRef = raw.path("providerRef").asText(null);
        String message = raw.path("message").asText(null);
        return new LinkResultDTO(status, requiresOtp, requiresSignature, providerRef, message);
    }

    @Override
    protected JsonNode fetchRechargeInitCall(String walletId, String authorizationHeader, JsonNode body) {
        var req = restClient.post()
                .uri(RECHARGE_INIT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
        if (authorizationHeader != null && !authorizationHeader.isBlank()) {
            req = req.header(HttpHeaders.AUTHORIZATION, authorizationHeader);
        }
        String corr = MDC.get("correlationId");
        if (corr != null && !corr.isBlank()) {
            req = req.header("X-Correlation-Id", corr);
        }
        return req.retrieve().body(JsonNode.class);
    }

    @Override
    protected RechargeInitResultDTO adaptRechargeInitResponse(JsonNode raw) {
        if (raw == null || raw.isNull()) return new RechargeInitResultDTO(null, null, null, null);
        String status = raw.path("status").asText(null);
        String redirectUrl = raw.path("redirectUrl").asText(null);
        String transactionId = raw.path("transactionId").asText(null);
        String message = raw.path("message").asText(null);
        return new RechargeInitResultDTO(status, redirectUrl, transactionId, message);
    }

    @Override
    public ProviderMetadata getMetadata() {
        return ProviderMetadata.of(PROVIDER_ID, "Bancolombia");
    }
}
