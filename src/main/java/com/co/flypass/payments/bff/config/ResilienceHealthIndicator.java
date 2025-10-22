package com.co.flypass.payments.bff.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component("circuitBreakers")
public final class ResilienceHealthIndicator implements HealthIndicator {

    private final CircuitBreakerRegistry cbRegistry;

    public ResilienceHealthIndicator(CircuitBreakerRegistry cbRegistry) {
        this.cbRegistry = cbRegistry;
    }

    @Override
    public Health health() {
        final Map<String, Object> details = new LinkedHashMap<>();
        boolean anyOpen = false;

        for (CircuitBreaker cb : cbRegistry.getAllCircuitBreakers()) {
            final var metrics = cb.getMetrics();
            final var state = cb.getState();

            final Map<String, Object> per = Map.of(
                    "state", state.name(),
                    "failureRate", metrics.getFailureRate(),
                    "bufferedCalls", metrics.getNumberOfBufferedCalls(),
                    "failedCalls", metrics.getNumberOfFailedCalls(),
                    "slowCalls", metrics.getNumberOfSlowCalls()
            );

            details.put(cb.getName(), per);
            if (state == CircuitBreaker.State.OPEN) {
                anyOpen = true;
            }
        }

        return anyOpen
                ? Health.down().withDetails(details).build()
                : Health.up().withDetails(details).build();
    }
}
