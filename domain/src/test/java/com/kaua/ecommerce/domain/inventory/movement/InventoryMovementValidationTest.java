package com.kaua.ecommerce.domain.inventory.movement;

import com.kaua.ecommerce.domain.TestValidationHandler;
import com.kaua.ecommerce.domain.UnitTest;
import com.kaua.ecommerce.domain.inventory.Inventory;
import com.kaua.ecommerce.domain.inventory.InventoryID;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InventoryMovementValidationTest extends UnitTest {

    @Test
    void givenInvalidNullInventoryID_whenCallNewInventoryMovement_shouldReturnDomainException() {
        final InventoryID aInventoryId = null;
        final var aSku = "sku";
        final var aQuantity = 10;
        final var aStatus = InventoryMovementStatus.IN;

        final var expectedErrorMessage = CommonErrorMessage.nullMessage("inventoryId");
        final var expectedErrorCount = 1;

        final var aInventory = InventoryMovement.newInventoryMovement(aInventoryId, aSku, aQuantity, aStatus);

        final var aTestValidationHandler = new TestValidationHandler();
        aInventory.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidBlankSku_whenCallNewInventoryMovement_shouldReturnDomainException() {
        final var aInventoryId = InventoryID.unique();
        final var aSku = " ";
        final var aQuantity = 10;
        final var aStatus = InventoryMovementStatus.IN;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("sku");
        final var expectedErrorCount = 1;

        final var aInventory = InventoryMovement.newInventoryMovement(aInventoryId, aSku, aQuantity, aStatus);

        final var aTestValidationHandler = new TestValidationHandler();
        aInventory.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNullSku_whenCallNewInventoryMovement_shouldReturnDomainException() {
        final var aInventoryId = InventoryID.unique();
        final String aSku = null;
        final var aQuantity = 10;
        final var aStatus = InventoryMovementStatus.IN;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("sku");
        final var expectedErrorCount = 1;

        final var aInventory = InventoryMovement.newInventoryMovement(aInventoryId, aSku, aQuantity, aStatus);

        final var aTestValidationHandler = new TestValidationHandler();
        aInventory.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidQuantityGreaterThanZero_whenCallNewInventoryMovement_shouldReturnDomainException() {
        final var aInventoryId = InventoryID.unique();
        final var aSku = "sku";
        final var aQuantity = 0;
        final var aStatus = InventoryMovementStatus.IN;

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("quantity", 0);
        final var expectedErrorCount = 1;

        final var aInventory = InventoryMovement.newInventoryMovement(aInventoryId, aSku, aQuantity, aStatus);

        final var aTestValidationHandler = new TestValidationHandler();
        aInventory.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNullStatus_whenCallNewInventoryMovement_shouldReturnDomainException() {
        final var aInventoryId = InventoryID.unique();
        final var aSku = "sku";
        final var aQuantity = 10;
        final InventoryMovementStatus aStatus = null;

        final var expectedErrorMessage = CommonErrorMessage.nullMessage("status");
        final var expectedErrorCount = 1;

        final var aInventory = InventoryMovement.newInventoryMovement(aInventoryId, aSku, aQuantity, aStatus);

        final var aTestValidationHandler = new TestValidationHandler();
        aInventory.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }
}
