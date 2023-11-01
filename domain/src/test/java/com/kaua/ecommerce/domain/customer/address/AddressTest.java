package com.kaua.ecommerce.domain.customer.address;

import com.kaua.ecommerce.domain.TestValidationHandler;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AddressTest {

    @Test
    void givenAValidValues_whenCallNewAddress_shouldReturnAddress() {
        final var aStreet = "street";
        final var aNumber = "123";
        final var aComplement = "complement";
        final var aDistrict = "Bairro";
        final var aCity = "city";
        final var aState = "state";
        final var aZipCode = "12345678";

        final var aAddress = Address.newAddress(
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode);

        Assertions.assertNotNull(aAddress.getId());
        Assertions.assertEquals(aStreet, aAddress.getStreet());
        Assertions.assertEquals(aNumber, aAddress.getNumber());
        Assertions.assertEquals(aComplement, aAddress.getComplement());
        Assertions.assertEquals(aDistrict,aAddress.getDistrict());
        Assertions.assertEquals(aZipCode, aAddress.getZipCode());

        Assertions.assertDoesNotThrow(() -> aAddress.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenAnInvalidNullStreet_whenCallNewAddress_shouldReturnDomainException() {
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("street");
        final var expectedErrorCount = 1;

        final var aAddress = Address.newAddress(
                null,
                "123",
                null,
                "Bairro",
                "city",
                "state",
                "12345678");

        final var aTestValidationHandler = new TestValidationHandler();
        aAddress.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
    }

    @Test
    void givenAnInvalidBlankStreet_whenCallNewAddress_shouldReturnDomainException() {
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("street");
        final var expectedErrorCount = 1;

        final var aAddress = Address.newAddress(
                " ",
                "123",
                null,
                "Bairro",
                "city",
                "state",
                "12345678");

        final var aTestValidationHandler = new TestValidationHandler();
        aAddress.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
    }

    @Test
    void givenAnInvalidNullNumber_whenCallNewAddress_shouldReturnDomainException() {
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("number");
        final var expectedErrorCount = 1;

        final var aAddress = Address.newAddress(
                "street",
                null,
                null,
                "Bairro",
                "city",
                "state",
                "12345678");

        final var aTestValidationHandler = new TestValidationHandler();
        aAddress.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
    }

    @Test
    void givenAnInvalidBlankNumber_whenCallNewAddress_shouldReturnDomainException() {
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("number");
        final var expectedErrorCount = 1;

        final var aAddress = Address.newAddress(
                "street",
                " ",
                null,
                "Bairro",
                "city",
                "state",
                "12345678");

        final var aTestValidationHandler = new TestValidationHandler();
        aAddress.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
    }

    @Test
    void givenAnInvalidNullDistrict_whenCallNewAddress_shouldReturnDomainException() {
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("district");
        final var expectedErrorCount = 1;

        final var aAddress = Address.newAddress(
                "street",
                "123",
                null,
                null,
                "city",
                "state",
                "12345678");

        final var aTestValidationHandler = new TestValidationHandler();
        aAddress.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
    }

    @Test
    void givenAnInvalidBlankDistrict_whenCallNewAddress_shouldReturnDomainException() {
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("district");
        final var expectedErrorCount = 1;

        final var aAddress = Address.newAddress(
                "street",
                "123",
                null,
                " ",
                "city",
                "state",
                "12345678");

        final var aTestValidationHandler = new TestValidationHandler();
        aAddress.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
    }

    @Test
    void givenAnInvalidNullCity_whenCallNewAddress_shouldReturnDomainException() {
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("city");
        final var expectedErrorCount = 1;

        final var aAddress = Address.newAddress(
                "street",
                "123",
                null,
                "Bairro",
                null,
                "state",
                "12345678");

        final var aTestValidationHandler = new TestValidationHandler();
        aAddress.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
    }

    @Test
    void givenAnInvalidBlankCity_whenCallNewAddress_shouldReturnDomainException() {
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("city");
        final var expectedErrorCount = 1;

        final var aAddress = Address.newAddress(
                "street",
                "123",
                null,
                "Bairro",
                " ",
                "state",
                "12345678");

        final var aTestValidationHandler = new TestValidationHandler();
        aAddress.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
    }

    @Test
    void givenAnInvalidNullState_whenCallNewAddress_shouldReturnDomainException() {
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("state");
        final var expectedErrorCount = 1;

        final var aAddress = Address.newAddress(
                "street",
                "123",
                null,
                "Bairro",
                "city",
                null,
                "12345678");

        final var aTestValidationHandler = new TestValidationHandler();
        aAddress.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
    }

    @Test
    void givenAnInvalidBlankState_whenCallNewAddress_shouldReturnDomainException() {
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("state");
        final var expectedErrorCount = 1;

        final var aAddress = Address.newAddress(
                "street",
                "123",
                null,
                "Bairro",
                "city",
                " ",
                "12345678");

        final var aTestValidationHandler = new TestValidationHandler();
        aAddress.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
    }

    @Test
    void givenAnInvalidNullZipCode_whenCallNewAddress_shouldReturnDomainException() {
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("zipCode");
        final var expectedErrorCount = 1;

        final var aAddress = Address.newAddress(
                "street",
                "123",
                null,
                "Bairro",
                "city",
                "state",
                null);

        final var aTestValidationHandler = new TestValidationHandler();
        aAddress.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
    }

    @Test
    void givenAnInvalidBlankZipCode_whenCallNewAddress_shouldReturnDomainException() {
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("zipCode");
        final var expectedErrorCount = 1;

        final var aAddress = Address.newAddress(
                "street",
                "123",
                null,
                "Bairro",
                "city",
                "state",
                " ");

        final var aTestValidationHandler = new TestValidationHandler();
        aAddress.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
    }

    @Test
    void givenAValidValues_whenCallWith_shouldReturnAddressObject() {
        final var aAddressId = "123";
        final var aStreet = "street";
        final var aNumber = "123";
        final var aComplement = "complement";
        final var aDistrict = "Bairro";
        final var aCity = "city";
        final var aState = "state";
        final var aZipCode = "12345678";
        final var aNow = InstantUtils.now();

        final var aAddress = Address.with(
                aAddressId,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode,
                aNow,
                aNow
        );

        Assertions.assertEquals(aAddressId, aAddress.getId().getValue());
        Assertions.assertEquals(aStreet, aAddress.getStreet());
        Assertions.assertEquals(aNumber, aAddress.getNumber());
        Assertions.assertEquals(aComplement, aAddress.getComplement());
        Assertions.assertEquals(aDistrict, aAddress.getDistrict());
        Assertions.assertEquals(aZipCode, aAddress.getZipCode());
        Assertions.assertEquals(aNow, aAddress.getCreatedAt());
        Assertions.assertEquals(aNow, aAddress.getUpdatedAt());

        Assertions.assertDoesNotThrow(() -> aAddress.validate(new ThrowsValidationHandler()));
    }

    @Test
    void testAddressIdEqualsAndHashCode() {
        final var aAddressId = AddressID.from("123456789");
        final var anotherAddressId = AddressID.from("123456789");

        Assertions.assertTrue(aAddressId.equals(anotherAddressId));
        Assertions.assertTrue(aAddressId.equals(aAddressId));
        Assertions.assertFalse(aAddressId.equals(null));
        Assertions.assertFalse(aAddressId.equals(""));
        Assertions.assertEquals(aAddressId.hashCode(), anotherAddressId.hashCode());
    }
}
