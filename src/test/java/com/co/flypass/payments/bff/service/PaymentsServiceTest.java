package com.co.flypass.payments.bff.service;

import com.co.flypass.payments.bff.exception.BusinessValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentsServiceTest {

    @Test
    void validateUserOwnsWallet_allows_owned_wallet() {
        var svc = new PaymentsService(null, null, null);
        assertDoesNotThrow(() -> svc.validateUserOwnsWallet("user", "user-1234"));
    }

    @Test
    void validateUserOwnsWallet_rejects_other_wallet() {
        var svc = new PaymentsService(null, null, null);
        var ex = assertThrows(BusinessValidationException.class, () -> svc.validateUserOwnsWallet("user", "other-1234"));
        assertEquals("WALLET_NOT_OWNED", ex.getCode());
    }

    @Test
    void validateUserPermissions_allows_read() {
        var svc = new PaymentsService(null, null, null);
        assertDoesNotThrow(() -> svc.validateUserPermissions("user", "READ_PAYMENT_METHODS"));
    }

    @Test
    void validateUserPermissions_denies_write() {
        var svc = new PaymentsService(null, null, null);
        var ex = assertThrows(BusinessValidationException.class, () -> svc.validateUserPermissions("user", "WRITE_PAYMENT_METHODS"));
        assertEquals("PERMISSION_DENIED", ex.getCode());
    }
}
