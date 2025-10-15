package com.co.flypass.payments.bff.config;

import jakarta.annotation.PostConstruct;
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
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Validated
@ConfigurationProperties(prefix = "payments")
public class PaymentsProperties {
    @NotBlank
    private final String defaultServiceId;

    @NotEmpty
    private final Map<String, @Valid ServiceConfig> servicesById;

    public PaymentsProperties(
            String defaultServiceId,
            Map<String, @Valid ServiceConfig> servicesById
    ) {
        this.defaultServiceId = normalize(defaultServiceId);

        Map<String, ServiceConfig> normalized =
                servicesById == null
                        ? Map.of()
                        : servicesById.entrySet().stream()
                        .collect(Collectors.toUnmodifiableMap(
                                e -> normalize(e.getKey()),
                                Map.Entry::getValue
                        ));

        this.servicesById = normalized;
    }

    @PostConstruct
    void validate() {
        if (servicesById.isEmpty()) {
            throw new IllegalStateException(
                    "Missing configuration: 'payments.services-by-id' must not be empty");
        }
        if (defaultServiceId == null || defaultServiceId.isBlank()) {
            throw new IllegalStateException(
                    "Missing configuration: 'payments.default-service-id' must not be blank");
        }
        if (!servicesById.containsKey(defaultServiceId)) {
            throw new IllegalStateException(
                    "Invalid configuration: 'payments.default-service-id' (" + defaultServiceId +
                            ") must match one key in 'payments.services-by-id': " + servicesById.keySet());
        }
    }

    public ServiceConfig require(String serviceId) {
        String id = normalize(serviceId);
        ServiceConfig cfg = servicesById.get(id);
        if (cfg == null) {
            throw new IllegalArgumentException(
                    "Service '" + serviceId + "' is not configured. Available: " + servicesById.keySet());
        }
        return cfg;
    }

    private static String normalize(String id) {
        return id == null ? null : id.trim().toLowerCase(Locale.ROOT);
    }

    @Getter
    public static class ServiceConfig {
        @NotBlank
        private final String baseUrl;

        @NotNull @DurationUnit(ChronoUnit.MILLIS)
        private final Duration connectTimeout;

        @NotNull @DurationUnit(ChronoUnit.MILLIS)
        private final Duration readTimeout;

        @Positive
        private final int maxAttemptsGet;

        public ServiceConfig(
                @NotBlank String baseUrl,
                @DefaultValue("1s") Duration connectTimeout,
                @DefaultValue("2s") Duration readTimeout,
                @DefaultValue("3") int maxAttemptsGet
        ) {
            this.baseUrl = baseUrl;
            this.connectTimeout = connectTimeout;
            this.readTimeout = readTimeout;
            this.maxAttemptsGet = maxAttemptsGet;
        }
    }
}
