package com.co.flypass.payments.bff.client;

import com.co.flypass.payments.bff.client.metadata.ProviderMetadata;
import com.co.flypass.payments.bff.exception.UpstreamUnavailableException;
import com.co.flypass.payments.bff.model.PaymentMethodListItem;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class AbstractServiceClientTest {

    @AfterEach
    void cleanupMdc() {
        MDC.clear();
    }

    static class TestClient extends AbstractServiceClient<String> {
        private final String provider;
        private final AtomicInteger attempts = new AtomicInteger();
        private final boolean failFirst;

        TestClient(String provider, SimpleMeterRegistry meterRegistry,
                   CircuitBreakerRegistry cbRegistry, RetryRegistry retryRegistry, boolean failFirst) {
            super(meterRegistry, cbRegistry, retryRegistry);
            this.provider = provider;
            this.failFirst = failFirst;
        }

        @Override
        protected String fetchPaymentMethods(String walletId, String authorizationHeader) throws Exception {
            int a = attempts.incrementAndGet();
            if (failFirst && a < 2) throw new RuntimeException("boom");
            return "ok";
        }

        @Override
        protected List<PaymentMethodListItem> adaptPaymentMethods(String raw) {
            return List.of(new PaymentMethodListItem("Visa", null, "Tarjeta crédito", true, "Titular", "**** **** **** 4242"));
        }

        @Override
        public ProviderMetadata getMetadata() {
            return ProviderMetadata.of(provider, provider);
        }
    }

    @Test
    void success_records_metric_and_cleans_mdc() {
        var meter = new SimpleMeterRegistry();
        var cbReg = CircuitBreakerRegistry.of(CircuitBreakerConfig.ofDefaults());
        var retryReg = RetryRegistry.of(RetryConfig.custom().maxAttempts(2).waitDuration(Duration.ofMillis(1)).build());

        var client = new TestClient("test", meter, cbReg, retryReg, false);
        MDC.put("correlationId", "existing");
        List<PaymentMethodListItem> result = client.listPaymentMethods("wallet-1234", "Bearer abc");
        assertNotNull(result);
        assertEquals(1, result.size());

        Timer t = meter.find("payments.client.list.latency").tags("provider", "test", "result", "success").timer();
        assertNotNull(t, "latency timer must be recorded");
        assertNotNull(meter.find("payments.client.list.count").tags("provider","test","result","success").counter(), "count counter must be recorded");
        assertNull(MDC.get("correlationId"), "correlationId must be cleaned");
    }

    @Test
    void error_records_error_metric_and_translates_exception() {
        var meter = new SimpleMeterRegistry();
        var cbReg = CircuitBreakerRegistry.of(CircuitBreakerConfig.ofDefaults());
        // Configure retry to a single attempt so the simulated first failure is propagated
        var retryReg = RetryRegistry.of(RetryConfig.custom().maxAttempts(1).waitDuration(Duration.ofMillis(1)).build());

        var client = new TestClient("test2", meter, cbReg, retryReg, true);
        assertThrows(RuntimeException.class, () -> client.listPaymentMethods("wallet-1", null));

        Timer t = meter.find("payments.client.list.latency").tags("provider", "test2", "result", "error").timer();
        assertNotNull(t, "error latency timer must be recorded");
        assertNotNull(meter.find("payments.client.list.count").tags("provider","test2","result","error").counter(), "count counter must be recorded on error");
        assertNull(MDC.get("correlationId"), "correlationId must be cleaned on error");
    }
}
