package com.co.flypass.payments.bff.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration(proxyBeanMethods = false)
public class ObservabilityConfig {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(Environment env) {
        final String app = env.getProperty("spring.application.name", "bff-payments");
        final String profile = env.getProperty("spring.profiles.active", "local");
        return registry -> registry.config().commonTags(
                "app", app,
                "env", profile
        );
    }
}
