package com.co.flypass.payments.bff.router;

public record RouteContext(
        String serviceIdFromQuery,
        String serviceIdFromHeader,
        String defaultServiceId
) {}
