package com.kaua.ecommerce.domain.order.validations;

import com.kaua.ecommerce.domain.TestValidationHandler;
import com.kaua.ecommerce.domain.UnitTest;
import com.kaua.ecommerce.domain.order.OrderDelivery;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderDeliveryValidationTest extends UnitTest {

    @Test
    void givenInvalidNullFreightType_whenCallNewOrderDelivery_shouldReturnDomainException() {
        final String aFreightType = null;
        final var aFreightPrice = 10.0f;
        final var aDeliveryEstimated = 10;
        final var aStreet = "Rua dos Bobos";
        final var aNumber = "1";
        final var aComplement = "Apto 101";
        final var aDistrict = "Centro";
        final var aCity = "São Paulo";
        final var aState = "SP";
        final var aZipCode = "12345-678";

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("freightType");

        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                aFreightType,
                aFreightPrice,
                aDeliveryEstimated,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode
        );

        final var aValidationHandler = new TestValidationHandler();
        aOrderDelivery.validate(aValidationHandler);

        Assertions.assertEquals(expectedErrorsCount, aValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidBlankFreightType_whenCallNewOrderDelivery_shouldReturnDomainException() {
        final String aFreightType = "";
        final var aFreightPrice = 10.0f;
        final var aDeliveryEstimated = 10;
        final var aStreet = "Rua dos Bobos";
        final var aNumber = "1";
        final var aComplement = "Apto 101";
        final var aDistrict = "Centro";
        final var aCity = "São Paulo";
        final var aState = "SP";
        final var aZipCode = "12345-678";

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("freightType");

        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                aFreightType,
                aFreightPrice,
                aDeliveryEstimated,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode
        );

        final var aValidationHandler = new TestValidationHandler();
        aOrderDelivery.validate(aValidationHandler);

        Assertions.assertEquals(expectedErrorsCount, aValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidZeroFreightPrice_whenCallNewOrderDelivery_shouldReturnDomainException() {
        final String aFreightType = "SEDEX";
        final var aFreightPrice = 0.0f;
        final var aDeliveryEstimated = 10;
        final var aStreet = "Rua dos Bobos";
        final var aNumber = "1";
        final var aComplement = "Apto 101";
        final var aDistrict = "Centro";
        final var aCity = "São Paulo";
        final var aState = "SP";
        final var aZipCode = "12345-678";

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.greaterThan("freightPrice", 0);

        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                aFreightType,
                aFreightPrice,
                aDeliveryEstimated,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode
        );

        final var aValidationHandler = new TestValidationHandler();
        aOrderDelivery.validate(aValidationHandler);

        Assertions.assertEquals(expectedErrorsCount, aValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidZeroDeliveryEstimated_whenCallNewOrderDelivery_shouldReturnDomainException() {
        final String aFreightType = "SEDEX";
        final var aFreightPrice = 10.0f;
        final var aDeliveryEstimated = 0;
        final var aStreet = "Rua dos Bobos";
        final var aNumber = "1";
        final var aComplement = "Apto 101";
        final var aDistrict = "Centro";
        final var aCity = "São Paulo";
        final var aState = "SP";
        final var aZipCode = "12345-678";

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.greaterThan("deliveryEstimated", 0);

        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                aFreightType,
                aFreightPrice,
                aDeliveryEstimated,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode
        );

        final var aValidationHandler = new TestValidationHandler();
        aOrderDelivery.validate(aValidationHandler);

        Assertions.assertEquals(expectedErrorsCount, aValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNullStreet_whenCallNewOrderDelivery_shouldReturnDomainException() {
        final String aFreightType = "SEDEX";
        final var aFreightPrice = 10.0f;
        final var aDeliveryEstimated = 10;
        final String aStreet = null;
        final var aNumber = "1";
        final var aComplement = "Apto 101";
        final var aDistrict = "Centro";
        final var aCity = "São Paulo";
        final var aState = "SP";
        final var aZipCode = "12345-678";

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("street");

        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                aFreightType,
                aFreightPrice,
                aDeliveryEstimated,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode
        );

        final var aValidationHandler = new TestValidationHandler();
        aOrderDelivery.validate(aValidationHandler);

        Assertions.assertEquals(expectedErrorsCount, aValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidBlankStreet_whenCallNewOrderDelivery_shouldReturnDomainException() {
        final String aFreightType = "SEDEX";
        final var aFreightPrice = 10.0f;
        final var aDeliveryEstimated = 10;
        final String aStreet = "";
        final var aNumber = "1";
        final var aComplement = "Apto 101";
        final var aDistrict = "Centro";
        final var aCity = "São Paulo";
        final var aState = "SP";
        final var aZipCode = "12345-678";

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("street");

        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                aFreightType,
                aFreightPrice,
                aDeliveryEstimated,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode
        );

        final var aValidationHandler = new TestValidationHandler();
        aOrderDelivery.validate(aValidationHandler);

        Assertions.assertEquals(expectedErrorsCount, aValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNullNumber_whenCallNewOrderDelivery_shouldReturnDomainException() {
        final String aFreightType = "SEDEX";
        final var aFreightPrice = 10.0f;
        final var aDeliveryEstimated = 10;
        final var aStreet = "Rua dos Bobos";
        final String aNumber = null;
        final var aComplement = "Apto 101";
        final var aDistrict = "Centro";
        final var aCity = "São Paulo";
        final var aState = "SP";
        final var aZipCode = "12345-678";

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("number");

        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                aFreightType,
                aFreightPrice,
                aDeliveryEstimated,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode
        );

        final var aValidationHandler = new TestValidationHandler();
        aOrderDelivery.validate(aValidationHandler);

        Assertions.assertEquals(expectedErrorsCount, aValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidBlankNumber_whenCallNewOrderDelivery_shouldReturnDomainException() {
        final String aFreightType = "SEDEX";
        final var aFreightPrice = 10.0f;
        final var aDeliveryEstimated = 10;
        final var aStreet = "Rua dos Bobos";
        String aNumber = "";
        final var aComplement = "Apto 101";
        final var aDistrict = "Centro";
        final var aCity = "São Paulo";
        final var aState = "SP";
        final var aZipCode = "12345-678";

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("number");

        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                aFreightType,
                aFreightPrice,
                aDeliveryEstimated,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode
        );

        final var aValidationHandler = new TestValidationHandler();
        aOrderDelivery.validate(aValidationHandler);

        Assertions.assertEquals(expectedErrorsCount, aValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidBlankComplement_whenCallNewOrderDelivery_shouldReturnDomainException() {
        final String aFreightType = "SEDEX";
        final var aFreightPrice = 10.0f;
        final var aDeliveryEstimated = 10;
        final var aStreet = "Rua dos Bobos";
        final var aNumber = "1";
        final String aComplement = "";
        final var aCity = "São Paulo";
        final var aDistrict = "Centro";
        final var aState = "SP";
        final var aZipCode = "12345-678";

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.blankMessage("complement");

        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                aFreightType,
                aFreightPrice,
                aDeliveryEstimated,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode
        );

        final var aValidationHandler = new TestValidationHandler();
        aOrderDelivery.validate(aValidationHandler);

        Assertions.assertEquals(expectedErrorsCount, aValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNullCity_whenCallNewOrderDelivery_shouldReturnDomainException() {
        final String aFreightType = "SEDEX";
        final var aFreightPrice = 10.0f;
        final var aDeliveryEstimated = 10;
        final var aStreet = "Rua dos Bobos";
        final var aNumber = "1";
        final var aComplement = "Apto 101";
        final var aDistrict = "Centro";
        final String aCity = null;
        final var aState = "SP";
        final var aZipCode = "12345-678";

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("city");

        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                aFreightType,
                aFreightPrice,
                aDeliveryEstimated,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode
        );

        final var aValidationHandler = new TestValidationHandler();
        aOrderDelivery.validate(aValidationHandler);

        Assertions.assertEquals(expectedErrorsCount, aValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidBlankCity_whenCallNewOrderDelivery_shouldReturnDomainException() {
        final String aFreightType = "SEDEX";
        final var aFreightPrice = 10.0f;
        final var aDeliveryEstimated = 10;
        final var aStreet = "Rua dos Bobos";
        final var aNumber = "1";
        final var aComplement = "Apto 101";
        final var aDistrict = "Centro";
        final String aCity = "";
        final var aState = "SP";
        final var aZipCode = "12345-678";

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("city");

        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                aFreightType,
                aFreightPrice,
                aDeliveryEstimated,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode
        );

        final var aValidationHandler = new TestValidationHandler();
        aOrderDelivery.validate(aValidationHandler);

        Assertions.assertEquals(expectedErrorsCount, aValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNullState_whenCallNewOrderDelivery_shouldReturnDomainException() {
        final String aFreightType = "SEDEX";
        final var aFreightPrice = 10.0f;
        final var aDeliveryEstimated = 10;
        final var aStreet = "Rua dos Bobos";
        final var aNumber = "1";
        final var aComplement = "Apto 101";
        final var aDistrict = "Centro";
        final var aCity = "São Paulo";
        final String aState = null;
        final var aZipCode = "12345-678";

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("state");

        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                aFreightType,
                aFreightPrice,
                aDeliveryEstimated,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode
        );

        final var aValidationHandler = new TestValidationHandler();
        aOrderDelivery.validate(aValidationHandler);

        Assertions.assertEquals(expectedErrorsCount, aValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidBlankState_whenCallNewOrderDelivery_shouldReturnDomainException() {
        final String aFreightType = "SEDEX";
        final var aFreightPrice = 10.0f;
        final var aDeliveryEstimated = 10;
        final var aStreet = "Rua dos Bobos";
        final var aNumber = "1";
        final var aComplement = "Apto 101";
        final var aDistrict = "Centro";
        final var aCity = "São Paulo";
        final String aState = "";
        final var aZipCode = "12345-678";

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("state");

        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                aFreightType,
                aFreightPrice,
                aDeliveryEstimated,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode
        );

        final var aValidationHandler = new TestValidationHandler();
        aOrderDelivery.validate(aValidationHandler);

        Assertions.assertEquals(expectedErrorsCount, aValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNullZipCode_whenCallNewOrderDelivery_shouldReturnDomainException() {
        final String aFreightType = "SEDEX";
        final var aFreightPrice = 10.0f;
        final var aDeliveryEstimated = 10;
        final var aStreet = "Rua dos Bobos";
        final var aNumber = "1";
        final var aComplement = "Apto 101";
        final var aDistrict = "Centro";
        final var aCity = "São Paulo";
        final var aState = "SP";
        final String aZipCode = null;

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("zipCode");

        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                aFreightType,
                aFreightPrice,
                aDeliveryEstimated,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode
        );

        final var aValidationHandler = new TestValidationHandler();
        aOrderDelivery.validate(aValidationHandler);

        Assertions.assertEquals(expectedErrorsCount, aValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidBlankZipCode_whenCallNewOrderDelivery_shouldReturnDomainException() {
        final String aFreightType = "SEDEX";
        final var aFreightPrice = 10.0f;
        final var aDeliveryEstimated = 10;
        final var aStreet = "Rua dos Bobos";
        final var aNumber = "1";
        final var aComplement = "Apto 101";
        final var aDistrict = "Centro";
        final var aCity = "São Paulo";
        final var aState = "SP";
        final String aZipCode = "";

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("zipCode");

        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                aFreightType,
                aFreightPrice,
                aDeliveryEstimated,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode
        );

        final var aValidationHandler = new TestValidationHandler();
        aOrderDelivery.validate(aValidationHandler);

        Assertions.assertEquals(expectedErrorsCount, aValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNullDistrict_whenCallNewOrderDelivery_shouldReturnDomainException() {
        final String aFreightType = "SEDEX";
        final var aFreightPrice = 10.0f;
        final var aDeliveryEstimated = 10;
        final var aStreet = "Rua dos Bobos";
        final var aNumber = "1";
        final var aComplement = "Apto 101";
        final String aDistrict = null;
        final var aCity = "São Paulo";
        final var aState = "SP";
        final var aZipCode = "12345-678";

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("district");

        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                aFreightType,
                aFreightPrice,
                aDeliveryEstimated,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode
        );

        final var aValidationHandler = new TestValidationHandler();
        aOrderDelivery.validate(aValidationHandler);

        Assertions.assertEquals(expectedErrorsCount, aValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidBlankDistrict_whenCallNewOrderDelivery_shouldReturnDomainException() {
        final String aFreightType = "SEDEX";
        final var aFreightPrice = 10.0f;
        final var aDeliveryEstimated = 10;
        final var aStreet = "Rua dos Bobos";
        final var aNumber = "1";
        final var aComplement = "Apto 101";
        String aDistrict = "";
        final var aCity = "São Paulo";
        final var aState = "SP";
        final var aZipCode = "12345-678";

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("district");

        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                aFreightType,
                aFreightPrice,
                aDeliveryEstimated,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode
        );

        final var aValidationHandler = new TestValidationHandler();
        aOrderDelivery.validate(aValidationHandler);

        Assertions.assertEquals(expectedErrorsCount, aValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aValidationHandler.getErrors().get(0).message());
    }
}
