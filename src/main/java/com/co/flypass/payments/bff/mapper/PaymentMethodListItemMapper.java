package com.co.flypass.payments.bff.mapper;

import com.co.flypass.payments.bff.client.bancolombia.dto.BancolombiaPaymentModeApiResponse;
import com.co.flypass.payments.bff.model.PaymentMethodListItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PaymentMethodListItemMapper {

    @Mapping(target = "brand",        source = "body.userPaymentMethod.franchise.name")
    @Mapping(target = "brandIconUrl", source = "body.userPaymentMethod.franchise.picture")
    @Mapping(target = "productLabel", source = "body.userPaymentMethod.productType.name",
            qualifiedByName = "extractProductLabelOrNull")
    @Mapping(target = "isDefault",    source = "body.selected")
    @Mapping(target = "holderName",   source = ".", qualifiedByName = "extractHolderNameOrNull")
    @Mapping(target = "maskedNumber", source = ".", qualifiedByName = "extractSuffixOrNull")
    PaymentMethodListItem toListItem(BancolombiaPaymentModeApiResponse src);

    @Named("extractProductLabelOrNull")
    default String extractProductLabelOrNull(String productTypeName) {
        if (productTypeName == null) return null;
        String t = productTypeName.trim();
        return t.isEmpty() ? null : t;
    }

    @Named("extractHolderNameOrNull")
    default String extractHolderNameOrNull(BancolombiaPaymentModeApiResponse src) {
        if (src == null || src.body() == null || src.body().userPaymentMethod() == null) return null;
        var upm = src.body().userPaymentMethod();
        if (upm.user() == null || upm.user().secureUser() == null || upm.user().secureUser().person() == null) {
            return null;
        }
        var p = upm.user().secureUser().person();
        if (p.fullName() != null && !p.fullName().isBlank()) return p.fullName().trim();
        String names = p.names() == null ? "" : p.names().trim();
        String surnames = p.surnames() == null ? "" : p.surnames().trim();
        String combined = (names + " " + surnames).trim();
        return combined.isEmpty() ? null : combined;
    }

    @Named("extractSuffixOrNull")
    default String extractSuffixOrNull(BancolombiaPaymentModeApiResponse src) {
        if (src == null || src.body() == null || src.body().userPaymentMethod() == null) return null;
        return src.body().userPaymentMethod().suffixAccount();
    }
}
