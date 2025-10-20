package com.co.flypass.payments.bff.router.resolve;

import com.co.flypass.payments.bff.router.RouteContext;
import com.co.flypass.payments.bff.router.ServiceId;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public final class DefaultPolicy implements RouteResolutionPolicy {
    @Override
    public @Nullable ServiceId resolve(@NonNull RouteContext routeContext) {
        String raw = routeContext.defaultServiceId();
        if (raw == null || raw.isBlank()) return null;
        return ServiceId.from(raw);
    }
}
