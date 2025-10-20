package com.co.flypass.payments.bff.client;

import com.co.flypass.payments.bff.client.metadata.ProviderMetadata;
import com.co.flypass.payments.bff.exception.UpstreamUnavailableException;
import com.co.flypass.payments.bff.model.PaymentMethodListItem;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;

public abstract class AbstractServiceClient<R> implements ServiceClient {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private static final String CORRELATION_ID_KEY = "correlationId";
    private static final String METRIC_COUNT = "payments.client.list.count";
    private static final String METRIC_LATENCY = "payments.client.list.latency";
    private static final String TAG_PROVIDER = "provider";
    private static final String TAG_RESULT = "result";
    private static final String RESULT_SUCCESS = "success";
    private static final String RESULT_ERROR = "error";

    private final MeterRegistry meterRegistry;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;

    protected AbstractServiceClient(MeterRegistry meterRegistry,
                                    CircuitBreakerRegistry circuitBreakerRegistry,
                                    RetryRegistry retryRegistry) {
        this.meterRegistry = meterRegistry;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.retryRegistry = retryRegistry;
    }

    @Override
    public final List<PaymentMethodListItem> listPaymentMethods(String walletId, String authorizationHeader) {
        if (walletId == null || walletId.isBlank()) {
            throw new IllegalArgumentException("walletId must not be blank");
        }

        final long start = System.nanoTime();
        final ProviderMetadata meta = getMetadata();
        final String providerId = Objects.requireNonNull(meta.providerId(), "providerId must not be null");

        final String previousCorr = setCorrelationIdIfAbsent();
        try {
            log.info("payments.list.start provider={} walletId_masked={} status=start",
                    providerId, maskWallet(walletId));

            final CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker(providerId);
            final Retry retry = retryRegistry.retry(providerId);

            final Callable<List<PaymentMethodListItem>> base =
                    () -> adaptPaymentMethods(fetchPaymentMethods(walletId, authorizationHeader));
            final Callable<List<PaymentMethodListItem>> withCb = CircuitBreaker.decorateCallable(cb, base);
            final Callable<List<PaymentMethodListItem>> withRetry = Retry.decorateCallable(retry, withCb);

            final List<PaymentMethodListItem> result = withRetry.call();
            recordMetrics(providerId, start, RESULT_SUCCESS);

            final long elapsedMs = Duration.ofNanos(System.nanoTime() - start).toMillis();
            log.info("payments.list.success provider={} walletId_masked={} elapsed_ms={} status=success",
                    providerId, maskWallet(walletId), elapsedMs);

            return result == null ? List.of() : result;
        } catch (Throwable t) {
            recordMetrics(providerId, start, RESULT_ERROR);

            final long elapsedMs = Duration.ofNanos(System.nanoTime() - start).toMillis();
            log.warn("payments.list.error provider={} walletId_masked={} elapsed_ms={} status=error msg={}",
                    providerId, maskWallet(walletId), elapsedMs, String.valueOf(t));

            throw translateException(t);
        } finally {
            restoreCorrelationId(previousCorr);
        }
    }

    protected abstract R fetchPaymentMethods(String walletId, String authorizationHeader) throws Exception;

    protected abstract List<PaymentMethodListItem> adaptPaymentMethods(R raw);

    public abstract ProviderMetadata getMetadata();

    protected RuntimeException translateException(Throwable t) {
        if (t instanceof RuntimeException re) return re;
        return new UpstreamUnavailableException(30, (t instanceof Exception) ? (Exception) t : new Exception(t));
    }

    private void recordMetrics(String providerId, long startNano, String outcome) {
        final long elapsedNanos = System.nanoTime() - startNano;
        Timer.builder(METRIC_LATENCY)
                .publishPercentileHistogram(true)
                .publishPercentiles(0.95, 0.99)
                .tag(TAG_PROVIDER, providerId)
                .tag(TAG_RESULT, outcome)
                .register(meterRegistry)
                .record(Duration.ofNanos(elapsedNanos));

        Counter.builder(METRIC_COUNT)
                .tag(TAG_PROVIDER, providerId)
                .tag(TAG_RESULT, outcome)
                .register(meterRegistry)
                .increment();
    }

    private static String setCorrelationIdIfAbsent() {
        final String existing = MDC.get(CORRELATION_ID_KEY);
        if (existing != null && !existing.isBlank()) {
            return existing;
        }
        MDC.put(CORRELATION_ID_KEY, UUID.randomUUID().toString());
        return null;
    }

    private static void restoreCorrelationId(String previous) {
        if (previous == null) {
            MDC.remove(CORRELATION_ID_KEY);
        } else {
            MDC.put(CORRELATION_ID_KEY, previous);
        }
    }

    private static String maskWallet(String walletId) {
        final int len = walletId.length();
        if (len <= 4) return "****";
        return "****" + walletId.substring(len - 4);
    }
}
