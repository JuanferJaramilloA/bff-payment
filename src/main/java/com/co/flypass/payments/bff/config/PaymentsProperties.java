package com.co.flypass.payments.bff.config;

import com.co.flypass.payments.bff.router.ServiceId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Validated
@ConfigurationProperties(prefix = "payments")
public final class PaymentsProperties {

    @NotBlank
    private final String defaultServiceId;

    @NotEmpty
    private final Map<String, @Valid ServiceConfig> servicesById;

    public PaymentsProperties(
            String defaultServiceId,
            Map<String, @Valid ServiceConfig> servicesById
    ) {
        final String normalizedDefault = ServiceId.normalize(defaultServiceId, true);
        final Map<String, ServiceConfig> normalizedMap =
                servicesById == null
                        ? Map.of()
                        : servicesById.entrySet().stream()
                        .collect(Collectors.toUnmodifiableMap(
                                e -> ServiceId.normalize(e.getKey(), true),
                                Map.Entry::getValue
                        ));

        if (normalizedDefault == null || normalizedDefault.isBlank()) {
            throw new IllegalStateException("Missing configuration: 'payments.default-service-id' must not be blank");
        }
        if (normalizedMap.isEmpty()) {
            throw new IllegalStateException("Missing configuration: 'payments.services-by-id' must not be empty");
        }
        if (!normalizedMap.containsKey(normalizedDefault)) {
            throw new IllegalStateException(
                    "Invalid configuration: 'payments.default-service-id' (" + normalizedDefault +
                            ") must match one key in 'payments.services-by-id': " + normalizedMap.keySet());
        }
        for (String key : normalizedMap.keySet()) {
            final String routerNorm = ServiceId.normalize(key, true);
            if (!key.equals(routerNorm)) {
                throw new IllegalStateException(
                        "Invalid key normalization for services-by-id: '" + key + "' should be '" + routerNorm + "'");
            }
        }

        this.defaultServiceId = normalizedDefault;
        this.servicesById = normalizedMap;
    }

    public ServiceConfig require(String serviceId) {
        final String id = ServiceId.normalize(serviceId, true);
        final ServiceConfig cfg = servicesById.get(id);
        if (cfg == null) {
            throw new IllegalArgumentException(
                    "Service '" + serviceId + "' is not configured. Available: " + servicesById.keySet());
        }
        return cfg;
    }

    @Getter
    public static final class ServiceConfig {
        @NotBlank
        private final String baseUrl;

        @NotNull
        @DurationUnit(ChronoUnit.MILLIS)
        private final Duration connectTimeout;

        @NotNull
        @DurationUnit(ChronoUnit.MILLIS)
        private final Duration readTimeout;

        @Positive
        private final int maxAttemptsGet;

        public ServiceConfig(
                @NotBlank String baseUrl,
                @DefaultValue("1s") Duration connectTimeout,
                @DefaultValue("2s") Duration readTimeout,
                @DefaultValue("3") int maxAttemptsGet
        ) {
            this.baseUrl = Objects.requireNonNull(baseUrl, "baseUrl must not be null").trim();
            this.connectTimeout = Objects.requireNonNull(connectTimeout, "connectTimeout must not be null");
            this.readTimeout = Objects.requireNonNull(readTimeout, "readTimeout must not be null");
            this.maxAttemptsGet = maxAttemptsGet;
        }
    }
}
