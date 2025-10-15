package com.co.flypass.payments.bff.router.resolve;

import com.co.flypass.payments.bff.router.RouteContext;
import com.co.flypass.payments.bff.router.ServiceId;

public class DefaultPolicy implements RouteResolutionPolicy {
    @Override public ServiceId resolve(RouteContext routeContext) {
        return ServiceId.from(routeContext.defaultServiceId());
    }
}
