package com.co.flypass.payments.bff.router.resolve;

import com.co.flypass.payments.bff.router.RouteContext;
import com.co.flypass.payments.bff.router.ServiceId;

import java.util.List;

public class RouteResolver {

    private final List<RouteResolutionPolicy> resolutionPoliciesInOrder;

    public RouteResolver(List<RouteResolutionPolicy> resolutionPoliciesInOrder) {
        this.resolutionPoliciesInOrder = List.copyOf(resolutionPoliciesInOrder);
    }

    public ServiceId resolve(RouteContext routeContext) {
        for (RouteResolutionPolicy policy : resolutionPoliciesInOrder) {
            ServiceId serviceId = policy.resolve(routeContext);
            if (serviceId != null) return serviceId;
        }
        return null;
    }
}
