package com.co.flypass.payments.bff.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(name = {
        "io.opentelemetry.api.OpenTelemetry",
        "io.opentelemetry.sdk.OpenTelemetrySdk"
})
@ConditionalOnProperty(prefix = "otel", name = "enabled", havingValue = "true", matchIfMissing = false)
public class OpenTelemetryConfig {

    private static final Logger log = LoggerFactory.getLogger(OpenTelemetryConfig.class);

    @Bean
    public ApplicationRunner openTelemetryBootstrap() {
        return args -> log.info("OpenTelemetry enabled (otel.enabled=true) and SDK present. Wire exporters here if needed.");
    }
}
