package com.co.flypass.payments.bff.adapter;

import com.co.flypass.payments.bff.client.external.dto.ExternalPaymentsApiResponse;
import com.co.flypass.payments.bff.model.PaymentMethodListItem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExternalPaymentsAdapterTest {

    @Test
    void maps_list_items_and_normalizes_brand_and_mask() {
        var adapter = new ExternalPaymentsAdapter();
        var dto = new ExternalPaymentsApiResponse(List.of(
                new ExternalPaymentsApiResponse.Item("VISA", "1234 5678 9123 0042", null, true, "", "http://icon1"),
                new ExternalPaymentsApiResponse.Item("MC", "0042", "Tarjeta débito", false, "Alice", "http://icon2")
        ));

        List<PaymentMethodListItem> out = adapter.adapt(dto);
        assertEquals(2, out.size());
        assertEquals("Visa", out.get(0).brand());
        assertEquals("****0042", out.get(0).maskedNumber());
        assertEquals("Titular", out.get(0).holderName());

        assertEquals("Mastercard", out.get(1).brand());
        assertEquals("****0042", out.get(1).maskedNumber());
        assertEquals("Alice", out.get(1).holderName());
        assertEquals("Tarjeta débito", out.get(1).productLabel());
    }
}
