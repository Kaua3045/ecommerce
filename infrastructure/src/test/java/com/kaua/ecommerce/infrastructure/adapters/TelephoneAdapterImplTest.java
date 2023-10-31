package com.kaua.ecommerce.infrastructure.adapters;

import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class TelephoneAdapterImplTest {

    @Autowired
    private TelephoneAdapterImpl telephoneAdapter;

    @Test
    void givenAValidTelephone_whenCallValidate_shouldReturnTrue() {
        final var aTelephone = "+55 (27) 2445-8092";

        final var actual = telephoneAdapter.validate(aTelephone);

        Assertions.assertTrue(actual);
    }

    @Test
    void givenAInvalidTelephone_whenCallValidate_shouldReturnFalse() {
        final var aTelephone = "+551199999999";

        final var actual = telephoneAdapter.validate(aTelephone);

        Assertions.assertFalse(actual);
    }

    @Test
    void givenAInvalidNullTelephone_whenCallValidate_shouldThrowDomainException() {
        final String aTelephone = null;

        Assertions.assertThrows(DomainException.class, () -> telephoneAdapter.validate(aTelephone));
    }

    @Test
    void givenAValidTelephone_whenCallFormatInternational_shouldReturnFormattedTelephone() {
        final var aRawTelephone = "+1 (123) 456-7890";
        final var aTelephone = "+11234567890";

        final var actual = telephoneAdapter.formatInternational(aRawTelephone);

        Assertions.assertEquals(aTelephone, actual);
    }

    @Test
    void givenAInvalidNullTelephone_whenCallFormatInternational_shouldThrowDomainException() {
        final String aTelephone = null;

        Assertions.assertThrows(DomainException.class, () -> telephoneAdapter.formatInternational(aTelephone));
    }

    @Test
    void givenAValidTelephone_whenCallFormatToCountry_shouldReturnFormattedTelephone() {
        final var aRawTelephone = "+15551234567";
        final var aTelephone = "+1 555-123-4567";

        final var actual = telephoneAdapter.formatToCountry(aRawTelephone, "US");

        Assertions.assertEquals(aTelephone, actual);
    }

    @Test
    void givenAnInvalidNullTelephone_whenCallFormatToCountry_shouldReturnNull() {
        final String aRawTelephone = null;

        final var actual = telephoneAdapter.formatToCountry(aRawTelephone, "US");

        Assertions.assertNull(actual);
    }

    @Test
    void givenAnInvalidBlankTelephone_whenCallFormatToCountry_shouldReturnNull() {
        final var aRawTelephone = " ";

        final var actual = telephoneAdapter.formatToCountry(aRawTelephone, "US");

        Assertions.assertNull(actual);
    }

    @Test
    void givenAnInvalidTelephone_whenCallFormatToCountry_shouldThrowDomainException() {
        final var aRawTelephone = "NAN";
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("telephone");

        final var actual = Assertions.assertThrows(DomainException.class,
                () -> telephoneAdapter.formatToCountry(aRawTelephone, "US"));

        Assertions.assertEquals(expectedErrorMessage, actual.getErrors().get(0).message());
    }
}
