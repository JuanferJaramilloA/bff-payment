package com.co.flypass.payments.bff.controller;

import com.co.flypass.payments.bff.model.PaymentMethodListItem;
import com.co.flypass.payments.bff.service.PaymentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Payments", description = "BFF de métodos de pago para Flypass")
public class PaymentsController {

    private final PaymentsService paymentsService;

    @Operation(
            summary = "Listar métodos de pago de una wallet",
            description = """
            Requiere autenticación (Bearer).
            Selección de proveedor:
            - Header `X-Service-Id`. Si no viene, se usa el default configurado.
            El token Bearer se propaga al proveedor cuando aplica.
            """
    )
    @GetMapping("/wallet/{walletId}/payment-methods")
    public List<PaymentMethodListItem> listPaymentMethods(
            @PathVariable @NotBlank String walletId,

            @Parameter(name = HttpHeaders.AUTHORIZATION, in = ParameterIn.HEADER, required = true,
                    description = "Token Bearer requerido. Se propaga al proveedor si aplica.")
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) String authorizationHeader,

            @Parameter(name = "X-Service-Id", in = ParameterIn.HEADER, required = false,
                    description = "Identificador del proveedor. Si se omite, se usa el default.")
            @RequestHeader(value = "X-Service-Id", required = false) String serviceIdFromHeader
    ) {
        return paymentsService.getPaymentMethods(
                walletId,
                authorizationHeader,
                serviceIdFromHeader
        );
    }
}
