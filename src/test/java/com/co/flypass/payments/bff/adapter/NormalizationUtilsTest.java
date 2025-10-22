package com.co.flypass.payments.bff.adapter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NormalizationUtilsTest {

    @Test
    void normalizeBrand_handles_common_cases_and_edges() {
        assertEquals("Mastercard", NormalizationUtils.normalizeBrand("MC"));
        assertEquals("Visa", NormalizationUtils.normalizeBrand("Visa"));
        assertEquals("American Express", NormalizationUtils.normalizeBrand("AMEX"));
        assertEquals("", NormalizationUtils.normalizeBrand(null));
        assertEquals("", NormalizationUtils.normalizeBrand("   "));
        assertEquals("Discover", NormalizationUtils.normalizeBrand("Discover"));
    }

    @Test
    void maskCardNumber_various_lengths() {
        assertEquals("****", NormalizationUtils.maskCardNumber(null));
        assertEquals("****", NormalizationUtils.maskCardNumber("123"));
        assertEquals("****0042", NormalizationUtils.maskCardNumber("0042"));
        assertEquals("****6789", NormalizationUtils.maskCardNumber("123456789"));
        assertEquals("****1111", NormalizationUtils.maskCardNumber("4111 1111 1111 1111"));
    }
}
