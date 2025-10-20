package com.co.flypass.payments.bff.adapter;

import com.co.flypass.payments.bff.client.bancolombia.dto.BancolombiaPaymentModeApiResponse;
import com.co.flypass.payments.bff.mapper.PaymentMethodListItemMapperImpl;
import com.co.flypass.payments.bff.model.PaymentMethodListItem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BancolombiaAdapterTest {

    @Test
    void maps_and_normalizes_mastercard() {
        var mapper = new PaymentMethodListItemMapperImpl();
        var adapter = new BancolombiaAdapter(mapper);

        var dto = new BancolombiaPaymentModeApiResponse(
                new BancolombiaPaymentModeApiResponse.Body(
                        new BancolombiaPaymentModeApiResponse.UserPaymentMethod(
                                new BancolombiaPaymentModeApiResponse.Franchise("MC", "http://icon"),
                                new BancolombiaPaymentModeApiResponse.ProductType("Crédito"),
                                "12345678904242",
                                new BancolombiaPaymentModeApiResponse.User(
                                        new BancolombiaPaymentModeApiResponse.SecureUser(
                                                new BancolombiaPaymentModeApiResponse.Person(null, "John", "Doe")
                                        )
                                )
                        ),
                        true
                )
        );

        List<PaymentMethodListItem> out = adapter.adapt(dto);
        assertEquals(1, out.size());
        PaymentMethodListItem item = out.get(0);
        assertEquals("Mastercard", item.brand());
        assertEquals("****4242", item.maskedNumber());
        assertEquals("Tarjeta crédito", item.productLabel());
    }

    @Test
    void maps_and_normalizes_visa() {
        var mapper = new PaymentMethodListItemMapperImpl();
        var adapter = new BancolombiaAdapter(mapper);

        var dto = new BancolombiaPaymentModeApiResponse(
                new BancolombiaPaymentModeApiResponse.Body(
                        new BancolombiaPaymentModeApiResponse.UserPaymentMethod(
                                new BancolombiaPaymentModeApiResponse.Franchise("VISA", "http://icon"),
                                new BancolombiaPaymentModeApiResponse.ProductType("Débito"),
                                "0042",
                                new BancolombiaPaymentModeApiResponse.User(
                                        new BancolombiaPaymentModeApiResponse.SecureUser(
                                                new BancolombiaPaymentModeApiResponse.Person("Jane Roe", null, null)
                                        )
                                )
                        ),
                        false
                )
        );

        var out = adapter.adapt(dto);
        assertEquals(1, out.size());
        var item = out.get(0);
        assertEquals("Visa", item.brand());
        assertEquals("****0042", item.maskedNumber());
        assertEquals("Tarjeta débito", item.productLabel());
        assertEquals("Jane Roe", item.holderName());
    }
}
