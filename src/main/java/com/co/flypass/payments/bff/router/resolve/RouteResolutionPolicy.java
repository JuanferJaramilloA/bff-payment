package com.co.flypass.payments.bff.router.resolve;

import com.co.flypass.payments.bff.router.RouteContext;
import com.co.flypass.payments.bff.router.ServiceId;

public interface RouteResolutionPolicy {
    ServiceId resolve(RouteContext routeContext);
}
