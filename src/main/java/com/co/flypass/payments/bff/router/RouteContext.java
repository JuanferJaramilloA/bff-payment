package com.co.flypass.payments.bff.router;

public record RouteContext(
        String serviceIdFromHeader,
        String defaultServiceId
) {}
