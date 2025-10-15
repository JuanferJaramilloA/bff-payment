package com.co.flypass.payments.bff.config;

import com.co.flypass.payments.bff.router.resolve.DefaultPolicy;
import com.co.flypass.payments.bff.router.resolve.HeaderPolicy;
import com.co.flypass.payments.bff.router.resolve.QueryParamPolicy;
import com.co.flypass.payments.bff.router.resolve.RouteResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RouteResolverConfig {
    @Bean
    RouteResolver routeResolver() {
        return new RouteResolver(List.of(
                new QueryParamPolicy(),
                new HeaderPolicy(),
                new DefaultPolicy()
        ));
    }
}
