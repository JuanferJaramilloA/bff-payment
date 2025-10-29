package com.co.flypass.payments.bff.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentMethodListItem(
        @JsonProperty("brand")
        String brand,

        @JsonProperty("brandIconUrl")
        String brandIconUrl,

        @JsonProperty("productLabel")
        String productLabel,

        @JsonProperty("isDefault")
        boolean isDefault,

        @JsonProperty("holderName")
        String holderName,

        @JsonProperty("maskedNumber")
        String maskedNumber
) {}
