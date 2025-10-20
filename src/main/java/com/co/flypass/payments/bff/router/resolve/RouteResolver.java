package com.co.flypass.payments.bff.router.resolve;

import com.co.flypass.payments.bff.router.RouteContext;
import com.co.flypass.payments.bff.router.ServiceId;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Objects;

public final class RouteResolver {

    private final List<RouteResolutionPolicy> resolutionPoliciesInOrder;

    public RouteResolver(@NonNull List<RouteResolutionPolicy> resolutionPoliciesInOrder) {
        this.resolutionPoliciesInOrder = List.copyOf(
                Objects.requireNonNull(resolutionPoliciesInOrder, "resolutionPoliciesInOrder must not be null")
        );
    }

    public @Nullable ServiceId resolve(@NonNull RouteContext routeContext) {
        for (RouteResolutionPolicy policy : resolutionPoliciesInOrder) {
            ServiceId serviceId = policy.resolve(routeContext);
            if (serviceId != null) return serviceId;
        }
        return null;
    }
}
