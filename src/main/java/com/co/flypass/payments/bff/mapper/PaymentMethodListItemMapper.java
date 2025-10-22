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

    @Mapping(target = "brand",        source = "body.userPaymentMethod.franchise.name", qualifiedByName = "normalizeBrand")
    @Mapping(target = "brandIconUrl", source = "body.userPaymentMethod.franchise.picture")
    @Mapping(target = "productLabel", source = "body.userPaymentMethod.productType.name",
            qualifiedByName = "normalizeProductLabel")
    @Mapping(target = "isDefault",    source = "body.selected")
    @Mapping(target = "holderName",   source = ".", qualifiedByName = "extractHolderNameOrDefault")
    @Mapping(target = "maskedNumber", source = ".", qualifiedByName = "extractAndMaskSuffix")
    PaymentMethodListItem toListItem(BancolombiaPaymentModeApiResponse src);

    @Named("normalizeBrand")
    default String normalizeBrand(String brand) {
        return com.co.flypass.payments.bff.adapter.NormalizationUtils.normalizeBrand(brand);
    }

    @Named("normalizeProductLabel")
    default String normalizeProductLabel(String productTypeName) {
        return com.co.flypass.payments.bff.adapter.NormalizationUtils.normalizeProductLabel(productTypeName, "");
    }

    @Named("extractHolderNameOrDefault")
    default String extractHolderNameOrDefault(BancolombiaPaymentModeApiResponse src) {
        if (src == null || src.body() == null || src.body().userPaymentMethod() == null) return "Titular";
        var upm = src.body().userPaymentMethod();
        if (upm.user() == null || upm.user().secureUser() == null || upm.user().secureUser().person() == null) {
            return "Titular";
        }
        var p = upm.user().secureUser().person();
        if (p.fullName() != null && !p.fullName().isBlank()) return p.fullName().trim();
        String names = p.names() == null ? "" : p.names().trim();
        String surnames = p.surnames() == null ? "" : p.surnames().trim();
        String combined = (names + " " + surnames).trim();
        return combined.isEmpty() ? "Titular" : combined;
    }

    @Named("extractAndMaskSuffix")
    default String extractAndMaskSuffix(BancolombiaPaymentModeApiResponse src) {
        if (src == null || src.body() == null || src.body().userPaymentMethod() == null) return "****";
        String suffix = src.body().userPaymentMethod().suffixAccount();
        return com.co.flypass.payments.bff.adapter.NormalizationUtils.maskCardNumber(suffix);
    }
}
