package com.co.flypass.payments.bff.controller;

import com.co.flypass.payments.bff.model.PaymentMethodListItem;
import com.co.flypass.payments.bff.service.PaymentsService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PaymentsController {

    private final PaymentsService paymentsService;

    @GetMapping("/wallet/{walletId}/payment-methods")
    public List<PaymentMethodListItem> listPaymentMethods(
            @PathVariable @NotBlank String walletId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestHeader(value = "X-Service", required = false) String serviceIdFromHeader,
            @RequestParam(value = "service", required = false) String serviceIdFromQuery
    ) {
        return paymentsService.getPaymentMethods(
                walletId,
                authorizationHeader,
                serviceIdFromQuery,
                serviceIdFromHeader
        );
    }
}
