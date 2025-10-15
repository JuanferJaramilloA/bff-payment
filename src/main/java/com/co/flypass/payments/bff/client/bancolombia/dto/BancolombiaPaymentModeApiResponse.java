package com.co.flypass.payments.bff.client.bancolombia.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BancolombiaPaymentModeApiResponse(
        Body body
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record Body(
            UserPaymentMethod userPaymentMethod,
            Boolean selected
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record UserPaymentMethod(
            Franchise franchise,
            ProductType productType,
            String suffixAccount,
            User user
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record Franchise(
            String name,
            String picture
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record ProductType(
            String name
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record User(
            SecureUser secureUser
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record SecureUser(
            Person person
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record Person(
            String fullName,
            String names,
            String surnames
    ) {}
}
