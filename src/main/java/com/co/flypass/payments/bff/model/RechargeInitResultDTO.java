package com.co.flypass.payments.bff.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RechargeInitResultDTO(
        @JsonProperty("status")
        String status,

        @JsonProperty("redirectUrl")
        String redirectUrl,

        @JsonProperty("transactionId")
        String transactionId,

        @JsonProperty("message")
        String message
) {}
