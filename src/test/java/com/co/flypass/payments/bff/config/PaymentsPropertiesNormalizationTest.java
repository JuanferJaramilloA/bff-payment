package com.co.flypass.payments.bff.config;

import com.co.flypass.payments.bff.router.ServiceId;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PaymentsPropertiesNormalizationTest {

    @Test
    void serviceId_and_properties_normalize_equally_bancolombia() {
        PaymentsProperties.ServiceConfig cfg = new PaymentsProperties.ServiceConfig(
                "http://example", Duration.ofMillis(500), Duration.ofMillis(1000), 2);
        PaymentsProperties props = new PaymentsProperties(
                "  Bancolombia  ",
                Map.of("  Bancolombia  ", cfg)
        );

        String normRouter = ServiceId.normalize("  Bancolombia  ", true);
        assertEquals("bancolombia", normRouter);
        assertEquals(normRouter, props.getDefaultServiceId());
        assertSame(cfg, props.require("B a n c o l o m b i a"));
    }

    @Test
    void serviceId_and_properties_normalize_equally_external() {
        PaymentsProperties.ServiceConfig cfg = new PaymentsProperties.ServiceConfig(
                "http://external", Duration.ofMillis(300), Duration.ofMillis(600), 3);
        PaymentsProperties props = new PaymentsProperties(
                "  External  ",
                Map.of("  External  ", cfg)
        );

        String normRouter = ServiceId.normalize("  External  ", true);
        assertEquals("external", normRouter);
        assertEquals(normRouter, props.getDefaultServiceId());
        assertSame(cfg, props.require(" e x t e r n a l "));
    }
}
