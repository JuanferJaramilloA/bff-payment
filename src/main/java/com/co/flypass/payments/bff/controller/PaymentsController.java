package com.co.flypass.payments.bff.controller;

import com.co.flypass.payments.bff.model.LinkResultDTO;
import com.co.flypass.payments.bff.model.PaymentMethodListItem;
import com.co.flypass.payments.bff.model.RechargeInitResultDTO;
import com.co.flypass.payments.bff.service.PaymentsService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PaymentsController {

    private final PaymentsService paymentsService;

    @GetMapping("/wallet/{walletId}/payment-methods")
    public List<PaymentMethodListItem> listPaymentMethods(
            @PathVariable @NotBlank String walletId,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) String authorizationHeader,
            @RequestHeader(value = "X-Service-Id", required = false) String serviceIdFromHeader
    ) {
        return paymentsService.getPaymentMethods(
                walletId,
                authorizationHeader,
                serviceIdFromHeader
        );
    }

    @PostMapping(value = "/wallet/{walletId}/payment-methods", consumes = MediaType.APPLICATION_JSON_VALUE)
    public LinkResultDTO linkPaymentMethod(
            @PathVariable @NotBlank String walletId,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) String authorizationHeader,
            @RequestHeader(value = "X-Service-Id", required = false) String serviceIdFromHeader,
            @RequestBody(required = false) JsonNode body
    ) {
        return paymentsService.linkPaymentMethod(walletId, authorizationHeader, serviceIdFromHeader, body);
    }

    @PostMapping(value = "/wallet/{walletId}/recharges/init", consumes = MediaType.APPLICATION_JSON_VALUE)
    public RechargeInitResultDTO rechargeInit(
            @PathVariable @NotBlank String walletId,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) String authorizationHeader,
            @RequestHeader(value = "X-Service-Id", required = false) String serviceIdFromHeader,
            @RequestBody(required = false) JsonNode body
    ) {
        return paymentsService.rechargeInit(walletId, authorizationHeader, serviceIdFromHeader, body);
    }
}
