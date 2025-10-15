package com.co.flypass.payments.bff.mapper;

import com.co.flypass.payments.bff.client.bancolombia.dto.BancolombiaPaymentModeApiResponse;
import com.co.flypass.payments.bff.model.PaymentMethodListItem;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentMethodListItemMapperTest {

    private final PaymentMethodListItemMapper mapper = Mappers.getMapper(PaymentMethodListItemMapper.class);

    @Test
    void productLabel_credit_vs_debit_case_and_accents() {
        var credit = dto("Mastercard", "assets/general/images/Mastercard.svg", "CrÉdiTo", true, "Juan Perez", "***8888");
        PaymentMethodListItem i1 = mapper.toListItem(credit);
        assertThat(i1.productLabel()).isEqualTo("Tarjeta crédito");

        var debit = dto("Visa", "assets/general/images/Visa.svg", "débito", false, "Ana", "xx0256");
        PaymentMethodListItem i2 = mapper.toListItem(debit);
        assertThat(i2.productLabel()).isEqualTo("Tarjeta débito");
    }

    @Test
    void holderName_prefers_fullName_then_names_surnames_then_default() {
        var withFullName = dtoWithPerson("Juan Fernando Jaramillo", null, null, true, "9999");
        assertThat(mapper.toListItem(withFullName).holderName()).isEqualTo("Juan Fernando Jaramillo");

        var withParts = dtoWithPerson("", "Carlos", "Ramírez", false, "7777");
        assertThat(mapper.toListItem(withParts).holderName()).isEqualTo("Carlos Ramírez");

        var empty = dtoWithPerson(null, null, null, false, null);
        assertThat(mapper.toListItem(empty).holderName()).isEqualTo("Titular");
    }

    @Test
    void maskedNumber_uses_last4_digits_and_cleans_non_numeric_and_null() {
        var one = dto("Visa", "assets/general/images/Visa.svg", "credito", true, "X", "AB12 34 56 78");
        assertThat(mapper.toListItem(one).maskedNumber()).isEqualTo("************5678");

        var two = dto("Amex", "assets/general/images/Amex.svg", "debito", false, "Y", null);
        assertThat(mapper.toListItem(two).maskedNumber()).isEqualTo("************");
    }

    @Test
    void isDefault_from_body_selected() {
        var s1 = dto("Visa", "assets/general/images/Visa.svg", "credito", true, "", "0256");
        assertThat(mapper.toListItem(s1).isDefault()).isTrue();
        var s2 = dto("Visa", "assets/general/images/Visa.svg", "credito", false, "", "0256");
        assertThat(mapper.toListItem(s2).isDefault()).isFalse();
    }

    // ---- builders ----
    private static BancolombiaPaymentModeApiResponse dto(String brand, String picture, String productType, boolean selected, String fullName, String suffix) {
        var person = new BancolombiaPaymentModeApiResponse.Person(fullName, null, null);
        var secureUser = new BancolombiaPaymentModeApiResponse.SecureUser(person);
        var user = new BancolombiaPaymentModeApiResponse.User(secureUser);
        var franchise = new BancolombiaPaymentModeApiResponse.Franchise(brand, picture);
        var pt = new BancolombiaPaymentModeApiResponse.ProductType(productType);
        var upm = new BancolombiaPaymentModeApiResponse.UserPaymentMethod(franchise, pt, suffix, user);
        var body = new BancolombiaPaymentModeApiResponse.Body(upm, selected);
        return new BancolombiaPaymentModeApiResponse(body);
    }

    private static BancolombiaPaymentModeApiResponse dtoWithPerson(String fullName, String names, String surnames, boolean selected, String suffix) {
        var person = new BancolombiaPaymentModeApiResponse.Person(fullName, names, surnames);
        var secureUser = new BancolombiaPaymentModeApiResponse.SecureUser(person);
        var user = new BancolombiaPaymentModeApiResponse.User(secureUser);
        var franchise = new BancolombiaPaymentModeApiResponse.Franchise("Visa", "assets/general/images/Visa.svg");
        var pt = new BancolombiaPaymentModeApiResponse.ProductType("credito");
        var upm = new BancolombiaPaymentModeApiResponse.UserPaymentMethod(franchise, pt, suffix, user);
        var body = new BancolombiaPaymentModeApiResponse.Body(upm, selected);
        return new BancolombiaPaymentModeApiResponse(body);
    }
}
