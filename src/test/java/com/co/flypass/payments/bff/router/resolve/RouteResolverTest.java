package com.co.flypass.payments.bff.router.resolve;

import com.co.flypass.payments.bff.router.RouteContext;
import com.co.flypass.payments.bff.router.ServiceId;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RouteResolverTest {

    @Test
    void uses_header_over_default() {
        var resolver = new RouteResolver(List.of(new HeaderPolicy(), new DefaultPolicy()));
        var ctx = new RouteContext(" Bancolombia ", "default-provider");
        ServiceId sid = resolver.resolve(ctx);
        assertEquals(ServiceId.from("bancolombia"), sid);
    }

    @Test
    void falls_back_to_default_when_header_blank() {
        var resolver = new RouteResolver(List.of(new HeaderPolicy(), new DefaultPolicy()));
        var ctx = new RouteContext("   ", "   EXTERNAL   ");
        ServiceId sid = resolver.resolve(ctx);
        assertEquals(ServiceId.from("external"), sid);
    }
}
