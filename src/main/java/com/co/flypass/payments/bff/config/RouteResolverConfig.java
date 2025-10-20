package com.co.flypass.payments.bff.config;

import com.co.flypass.payments.bff.router.resolve.DefaultPolicy;
import com.co.flypass.payments.bff.router.resolve.HeaderPolicy;
import com.co.flypass.payments.bff.router.resolve.QueryParamPolicy;
import com.co.flypass.payments.bff.router.resolve.RouteResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class RouteResolverConfig {

    @Bean
    public QueryParamPolicy queryParamPolicy() {
        return new QueryParamPolicy();
    }

    @Bean
    public HeaderPolicy headerPolicy() {
        return new HeaderPolicy();
    }

    @Bean
    public DefaultPolicy defaultPolicy() {
        return new DefaultPolicy();
    }

    @Bean
    public RouteResolver routeResolver(
            QueryParamPolicy queryParamPolicy,
            HeaderPolicy headerPolicy,
            DefaultPolicy defaultPolicy
    ) {
        return new RouteResolver(List.of(
                queryParamPolicy,
                headerPolicy,
                defaultPolicy
        ));
    }
}
