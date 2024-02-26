package com.kaua.ecommerce.domain.inventory;

import com.kaua.ecommerce.domain.TestValidationHandler;
import com.kaua.ecommerce.domain.UnitTest;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InventoryValidationTest extends UnitTest {

    @Test
    void givenInvalidBlankProductId_whenCallNewInventory_shouldReturnDomainException() {
        final var aProductId = " ";
        final var aSku = "sku";
        final var aQuantity = 10;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("productId");
        final var expectedErrorCount = 1;

        final var aInventory = Inventory.newInventory(aProductId, aSku, aQuantity);

        final var aTestValidationHandler = new TestValidationHandler();
        aInventory.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNullProductId_whenCallNewInventory_shouldReturnDomainException() {
        final String aProductId = null;
        final var aSku = "sku";
        final var aQuantity = 10;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("productId");
        final var expectedErrorCount = 1;

        final var aInventory = Inventory.newInventory(aProductId, aSku, aQuantity);

        final var aTestValidationHandler = new TestValidationHandler();
        aInventory.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidBlankSku_whenCallNewInventory_shouldReturnDomainException() {
        final var aProductId = "1";
        final var aSku = " ";
        final var aQuantity = 10;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("sku");
        final var expectedErrorCount = 1;

        final var aInventory = Inventory.newInventory(aProductId, aSku, aQuantity);

        final var aTestValidationHandler = new TestValidationHandler();
        aInventory.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNullSku_whenCallNewInventory_shouldReturnDomainException() {
        final var aProductId = "1";
        final String aSku = null;
        final var aQuantity = 10;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("sku");
        final var expectedErrorCount = 1;

        final var aInventory = Inventory.newInventory(aProductId, aSku, aQuantity);

        final var aTestValidationHandler = new TestValidationHandler();
        aInventory.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidQuantityLessThanZero_whenCallNewInventory_shouldReturnDomainException() {
        final var aProductId = "1";
        final var aSku = "sku";
        final var aQuantity = -1;

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("quantity", -1);
        final var expectedErrorCount = 1;

        final var aInventory = Inventory.newInventory(aProductId, aSku, aQuantity);

        final var aTestValidationHandler = new TestValidationHandler();
        aInventory.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }
}
