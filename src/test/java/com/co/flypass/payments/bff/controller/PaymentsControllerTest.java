package com.co.flypass.payments.bff.controller;

import com.co.flypass.payments.bff.model.PaymentMethodListItem;
import com.co.flypass.payments.bff.service.PaymentsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentsController.class)
class PaymentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class TestConfig {
        @Bean
        PaymentsService paymentsService() {
            return new PaymentsService(null, null) {
                @Override
                public List<PaymentMethodListItem> getPaymentMethods(String walletId, String authorization, org.springframework.http.HttpHeaders headers, String connectorQuery) {
                    return List.of(
                            new PaymentMethodListItem(
                                    "Mastercard",
                                    "assets/general/images/Mastercard.svg",
                                    "Tarjeta crédito",
                                    true,
                                    "Juan Fernando Jaramillo",
                                    "************8888"
                            )
                    );
                }
            };
        }
    }

    @Test
    void getPaymentMethods_returns_list_of_listItems() throws Exception {
        mockMvc.perform(get("/wallet/{walletId}/payment-methods", "w123")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer abc")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].brand").value("Mastercard"))
                .andExpect(jsonPath("$[0].brandIconUrl").value("assets/general/images/Mastercard.svg"))
                .andExpect(jsonPath("$[0].productLabel").value("Tarjeta crédito"))
                .andExpect(jsonPath("$[0].isDefault").value(true))
                .andExpect(jsonPath("$[0].holderName").value("Juan Fernando Jaramillo"))
                .andExpect(jsonPath("$[0].maskedNumber").value("************8888"));
    }
}
