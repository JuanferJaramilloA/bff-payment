package com.co.flypass.payments.bff.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "PaymentMethodListItem")
public record PaymentMethodListItem(
        @JsonProperty("brand")
        @Schema(description = "Marca normalizada de la tarjeta", example = "Visa")
        String brand,

        @JsonProperty("brandIconUrl")
        @Schema(description = "URL del ícono de la marca", example = "https://cdn.flypass.co/icons/visa.svg")
        String brandIconUrl,

        @JsonProperty("productLabel")
        @Schema(description = "Etiqueta de producto normalizada", example = "Tarjeta crédito")
        String productLabel,

        @JsonProperty("isDefault")
        @Schema(description = "Indica si es el método de pago por defecto", example = "false")
        boolean isDefault,

        @JsonProperty("holderName")
        @Schema(description = "Nombre del titular", example = "Juan Pérez")
        String holderName,

        @JsonProperty("maskedNumber")
        @Schema(description = "Número enmascarado (máscara fija + last4)", example = "************1234")
        String maskedNumber
) {}
