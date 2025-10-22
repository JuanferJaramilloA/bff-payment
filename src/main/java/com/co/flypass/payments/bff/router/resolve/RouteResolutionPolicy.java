package com.co.flypass.payments.bff.router.resolve;

import com.co.flypass.payments.bff.router.RouteContext;
import com.co.flypass.payments.bff.router.ServiceId;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public interface RouteResolutionPolicy {
    @Nullable
    ServiceId resolve(@NonNull RouteContext routeContext);
}
