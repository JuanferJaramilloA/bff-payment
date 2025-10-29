package com.co.flypass.payments.bff.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LinkResultDTO(
        @JsonProperty("status")
        String status,

        @JsonProperty("requiresOtp")
        Boolean requiresOtp,

        @JsonProperty("requiresSignature")
        Boolean requiresSignature,

        @JsonProperty("providerRef")
        String providerRef,

        @JsonProperty("message")
        String message
) {}
