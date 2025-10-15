package com.co.flypass.payments.bff.mapper;

import com.co.flypass.payments.bff.client.bancolombia.dto.BancolombiaPaymentModeApiResponse;
import com.co.flypass.payments.bff.model.PaymentMethodListItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface PaymentMethodListItemMapper {

    @Mapping(target = "brand",        source = "body.userPaymentMethod.franchise.name")
    @Mapping(target = "brandIconUrl", source = "body.userPaymentMethod.franchise.picture")
    @Mapping(target = "productLabel", source = "body.userPaymentMethod.productType.name",
            qualifiedByName = "toProductLabel")
    @Mapping(target = "isDefault",    source = "body.selected")
    @Mapping(target = "holderName",   source = ".", qualifiedByName = "resolveHolderNameFromRoot")
    @Mapping(target = "maskedNumber", source = "body.userPaymentMethod.suffixAccount",
            qualifiedByName = "maskFromSuffix")
    PaymentMethodListItem toListItem(BancolombiaPaymentModeApiResponse src);

    @Named("toProductLabel")
    default String toProductLabel(String productTypeName) {
        if (productTypeName == null || productTypeName.isBlank()) return "Tarjeta crédito";
        String n = productTypeName.toLowerCase();
        return (n.contains("débito") || n.contains("debito")) ? "Tarjeta débito" : "Tarjeta crédito";
    }

    @Named("resolveHolderNameFromRoot")
    default String resolveHolderNameFromRoot(BancolombiaPaymentModeApiResponse src) {
        if (src == null || src.body() == null || src.body().userPaymentMethod() == null) return "Titular";
        var upm = src.body().userPaymentMethod();
        if (upm.user() == null || upm.user().secureUser() == null || upm.user().secureUser().person() == null) {
            return "Titular";
        }
        var p = upm.user().secureUser().person();
        if (p.fullName() != null && !p.fullName().isBlank()) return p.fullName();
        String names = p.names() == null ? "" : p.names();
        String surnames = p.surnames() == null ? "" : p.surnames();
        String combined = (names + " " + surnames).trim();
        return combined.isBlank() ? "Titular" : combined;
    }

    @Named("maskFromSuffix")
    default String maskFromSuffix(String suffix) {
        if (suffix == null) return "************";
        String digits = suffix.replaceAll("\\D", "");
        if (digits.length() > 4) digits = digits.substring(digits.length() - 4);
        return "************" + digits;
    }
}
