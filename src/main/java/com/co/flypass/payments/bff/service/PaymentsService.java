package com.co.flypass.payments.bff.service;

import com.co.flypass.payments.bff.client.ServiceClient;
import com.co.flypass.payments.bff.client.ServiceClientRegistry;
import com.co.flypass.payments.bff.config.PaymentsProperties;
import com.co.flypass.payments.bff.exception.BusinessValidationException;
import com.co.flypass.payments.bff.model.PaymentMethodListItem;
import com.co.flypass.payments.bff.router.RouteContext;
import com.co.flypass.payments.bff.router.ServiceId;
import com.co.flypass.payments.bff.router.resolve.RouteResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PaymentsService {

    private final RouteResolver routeResolver;
    private final ServiceClientRegistry serviceClientRegistry;
    private final PaymentsProperties paymentsProperties;

    public List<PaymentMethodListItem> getPaymentMethods(
            String walletId,
            String authorizationHeader,
            String serviceIdFromQuery,
            String serviceIdFromHeader
    ) {
        validateWalletId(walletId);
        String userId = extractUserId(authorizationHeader);
        validateUserId(userId);
        validateUserPermissions(userId, "READ_PAYMENT_METHODS");

        RouteContext routeContext = new RouteContext(
                serviceIdFromQuery,
                serviceIdFromHeader,
                paymentsProperties.getDefaultServiceId()
        );

        ServiceId selectedServiceId = Objects.requireNonNull(
                routeResolver.resolve(routeContext),
                "serviceId resolution returned null"
        );

        ServiceClient serviceClient = serviceClientRegistry.get(selectedServiceId);
        return serviceClient.listPaymentMethods(walletId, authorizationHeader);
    }

    private static void validateWalletId(String walletId) {
        if (walletId == null || walletId.isBlank()) {
            throw BusinessValidationException.badRequest("WALLET_ID_REQUIRED", "walletId must not be blank");
        }
    }

    private static void validateUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw BusinessValidationException.unauthorized("USER_UNAUTHENTICATED", "User is not authenticated");
        }
    }

    private static String extractUserId(String authorizationHeader) {
        return (authorizationHeader == null || authorizationHeader.isBlank()) ? null : "user";
    }

    public static void validateUserPermissions(String userId, String permission) {
        if (permission == null || permission.isBlank()) {
            throw BusinessValidationException.badRequest("PERMISSION_REQUIRED", "permission must not be blank");
        }
        boolean allowed = switch (permission) {
            case "READ_PAYMENT_METHODS" -> true;
            default -> false;
        };
        if (!allowed) {
            throw BusinessValidationException.forbidden("PERMISSION_DENIED",
                    "User lacks required permission: " + permission);
        }
    }
}
