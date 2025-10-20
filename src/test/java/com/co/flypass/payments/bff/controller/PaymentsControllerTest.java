package com.co.flypass.payments.bff.controller;

import com.co.flypass.payments.bff.exception.UnknownConnectorException;
import com.co.flypass.payments.bff.model.PaymentMethodListItem;
import com.co.flypass.payments.bff.service.PaymentsService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentsController.class)
class PaymentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentsService paymentsService;

    @Test
    void getPaymentMethods_returns_list_of_listItems() throws Exception {
        when(paymentsService.getPaymentMethods(eq("w123"), eq("Bearer abc"), org.mockito.ArgumentMatchers.nullable(String.class), org.mockito.ArgumentMatchers.nullable(String.class)))
                .thenReturn(List.of(new PaymentMethodListItem(
                        "Mastercard",
                        "assets/general/images/Mastercard.svg",
                        "Tarjeta crédito",
                        true,
                        "Juan Fernando Jaramillo",
                        "****8888"
                )));

        mockMvc.perform(get("/wallet/{walletId}/payment-methods", "w123")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer abc")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].brand").value("Mastercard"))
                .andExpect(jsonPath("$[0].brandIconUrl").value("assets/general/images/Mastercard.svg"))
                .andExpect(jsonPath("$[0].productLabel").value("Tarjeta crédito"))
                .andExpect(jsonPath("$[0].isDefault").value(true))
                .andExpect(jsonPath("$[0].holderName").value("Juan Fernando Jaramillo"))
                .andExpect(jsonPath("$[0].maskedNumber").value("****8888"));
    }

    @Test
    void query_param_wins_over_header_when_both_present_mapping_to_service_layer() throws Exception {
        when(paymentsService.getPaymentMethods(eq("wid"), eq("Bearer tk"), anyString(), anyString()))
                .thenReturn(List.of());

        mockMvc.perform(get("/wallet/{walletId}/payment-methods", "wid")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer tk")
                        .header("X-Service-Id", "bancolombia")
                        .queryParam("connector", "external")
                )
                .andExpect(status().isOk());

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
        verify(paymentsService).getPaymentMethods(eq("wid"), eq("Bearer tk"), queryCaptor.capture(), headerCaptor.capture());
        // Controller must pass both; precedence is applied in service/router, which is separately unit-tested
        org.junit.jupiter.api.Assertions.assertEquals("external", queryCaptor.getValue());
        org.junit.jupiter.api.Assertions.assertEquals("bancolombia", headerCaptor.getValue());
    }

    @Test
    void unknown_provider_returns_400_with_expected_body() throws Exception {
        when(paymentsService.getPaymentMethods(eq("w999"), eq("Bearer abc"), eq("unknown"), org.mockito.ArgumentMatchers.nullable(String.class)))
                .thenThrow(new UnknownConnectorException("unknown", Set.of("bancolombia", "external")));

        mockMvc.perform(get("/wallet/{walletId}/payment-methods", "w999")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer abc")
                        .queryParam("connector", "unknown")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_CONNECTOR"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("unknown")));
    }
}
