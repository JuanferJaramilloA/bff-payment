package com.co.flypass.payments.bff.router;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServiceIdTest {

    @Test
    void normalize_removes_spaces_and_lowercases() {
        assertEquals("bancolombia", ServiceId.normalize(" Bancolombia "));
        assertEquals("myservice", ServiceId.normalize(" My Service "));
    }

    @Test
    void equals_and_hash_by_value() {
        ServiceId a = ServiceId.from("BANCOLOMBIA");
        ServiceId b = ServiceId.from("b a n c o l o m b i a");
        assertNotNull(a);
        assertNotNull(b);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals("bancolombia", a.toString());
    }

    @Test
    void null_or_blank_inputs_throw() {
        assertThrows(IllegalArgumentException.class, () -> ServiceId.from(null));
        assertThrows(IllegalArgumentException.class, () -> ServiceId.from("   "));
    }
}
