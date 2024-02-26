package com.kaua.ecommerce.domain.inventory.movement;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.UnitTest;
import com.kaua.ecommerce.domain.inventory.InventoryID;
import com.kaua.ecommerce.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InventoryMovementTest extends UnitTest {

    @Test
    void givenAValidValuesAndStatusIn_whenCallNewInventoryMovement_thenShouldBeCreated() {
        final var aInventoryID = InventoryID.unique();
        final var aSku = "SKU-123";
        final var aQuantity = 10;
        final var aStatus = InventoryMovementStatus.IN;

        final var aInventoryMovement = InventoryMovement
                .newInventoryMovement(aInventoryID, aSku, aQuantity, aStatus);

        Assertions.assertNotNull(aInventoryMovement);
        Assertions.assertNotNull(aInventoryMovement.getId());
        Assertions.assertEquals(aInventoryID, aInventoryMovement.getInventoryId());
        Assertions.assertEquals(aSku, aInventoryMovement.getSku());
        Assertions.assertEquals(aQuantity, aInventoryMovement.getQuantity());
        Assertions.assertEquals(aStatus, aInventoryMovement.getStatus());
        Assertions.assertNotNull(aInventoryMovement.getCreatedAt());
        Assertions.assertNotNull(aInventoryMovement.getUpdatedAt());
        Assertions.assertDoesNotThrow(() -> aInventoryMovement.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenAValidValuesAndStatusOUT_whenCallNewInventoryMovement_thenShouldBeCreated() {
        final var aInventoryID = InventoryID.unique();
        final var aSku = "SKU-123";
        final var aQuantity = 10;
        final var aStatus = InventoryMovementStatus.OUT;

        final var aInventoryMovement = InventoryMovement
                .newInventoryMovement(aInventoryID, aSku, aQuantity, aStatus);

        Assertions.assertNotNull(aInventoryMovement);
        Assertions.assertNotNull(aInventoryMovement.getId());
        Assertions.assertEquals(aInventoryID, aInventoryMovement.getInventoryId());
        Assertions.assertEquals(aSku, aInventoryMovement.getSku());
        Assertions.assertEquals(aQuantity, aInventoryMovement.getQuantity());
        Assertions.assertEquals(aStatus, aInventoryMovement.getStatus());
        Assertions.assertNotNull(aInventoryMovement.getCreatedAt());
        Assertions.assertNotNull(aInventoryMovement.getUpdatedAt());
        Assertions.assertDoesNotThrow(() -> aInventoryMovement.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenAValidValuesAndStatusREMOVED_whenCallNewInventoryMovement_thenShouldBeCreated() {
        final var aInventoryID = InventoryID.unique();
        final var aSku = "SKU-123";
        final var aQuantity = 10;
        final var aStatus = InventoryMovementStatus.REMOVED;

        final var aInventoryMovement = InventoryMovement
                .newInventoryMovement(aInventoryID, aSku, aQuantity, aStatus);

        Assertions.assertNotNull(aInventoryMovement);
        Assertions.assertNotNull(aInventoryMovement.getId());
        Assertions.assertEquals(aInventoryID, aInventoryMovement.getInventoryId());
        Assertions.assertEquals(aSku, aInventoryMovement.getSku());
        Assertions.assertEquals(aQuantity, aInventoryMovement.getQuantity());
        Assertions.assertEquals(aStatus, aInventoryMovement.getStatus());
        Assertions.assertNotNull(aInventoryMovement.getCreatedAt());
        Assertions.assertNotNull(aInventoryMovement.getUpdatedAt());
        Assertions.assertDoesNotThrow(() -> aInventoryMovement.validate(new ThrowsValidationHandler()));
    }

    @Test
    void testInventoryMovementIdEqualsAndHashCode() {
        final var aInventoryMovementId = InventoryMovementID.from("123456789");
        final var anotherInventoryMovementId = InventoryMovementID.from("123456789");

        Assertions.assertTrue(aInventoryMovementId.equals(anotherInventoryMovementId));
        Assertions.assertTrue(aInventoryMovementId.equals(aInventoryMovementId));
        Assertions.assertFalse(aInventoryMovementId.equals(null));
        Assertions.assertFalse(aInventoryMovementId.equals(""));
        Assertions.assertEquals(aInventoryMovementId.hashCode(), anotherInventoryMovementId.hashCode());
    }

    @Test
    void givenAValidName_whenCallInventoryMovementStatusOf_thenShouldReturnAStatus() {
        final var status = InventoryMovementStatus.of("IN");

        Assertions.assertTrue(status.isPresent());
        Assertions.assertEquals(InventoryMovementStatus.IN, status.get());
    }

    @Test
    void givenAValidInventoryMovement_whenCallToString_shouldReturnString() {
        final var aInventoryMovement = Fixture.InventoriesMovements.in();

        final var aInventoryMovementString = aInventoryMovement.toString();

        Assertions.assertTrue(aInventoryMovementString.contains(aInventoryMovement.getId().getValue()));
        Assertions.assertTrue(aInventoryMovementString.contains(aInventoryMovement.getInventoryId().getValue()));
        Assertions.assertTrue(aInventoryMovementString.contains(aInventoryMovement.getSku()));
        Assertions.assertTrue(aInventoryMovementString.contains(String.valueOf(aInventoryMovement.getQuantity())));
        Assertions.assertTrue(aInventoryMovementString.contains(aInventoryMovement.getStatus().name()));
        Assertions.assertTrue(aInventoryMovementString.contains(aInventoryMovement.getCreatedAt().toString()));
        Assertions.assertTrue(aInventoryMovementString.contains(aInventoryMovement.getUpdatedAt().toString()));
    }

    @Test
    void givenAValidInventoryMovement_whenCallWith_shouldReturnNewInventoryMovement() {
        final var aInventoryMovement = Fixture.InventoriesMovements.out();

        final var aNewInventoryMovement = InventoryMovement.with(aInventoryMovement);

        Assertions.assertEquals(aInventoryMovement.getId(), aNewInventoryMovement.getId());
        Assertions.assertEquals(aInventoryMovement.getInventoryId(), aNewInventoryMovement.getInventoryId());
        Assertions.assertEquals(aInventoryMovement.getSku(), aNewInventoryMovement.getSku());
        Assertions.assertEquals(aInventoryMovement.getQuantity(), aNewInventoryMovement.getQuantity());
        Assertions.assertEquals(aInventoryMovement.getStatus(), aNewInventoryMovement.getStatus());
        Assertions.assertEquals(aInventoryMovement.getCreatedAt(), aNewInventoryMovement.getCreatedAt());
        Assertions.assertEquals(aInventoryMovement.getUpdatedAt(), aNewInventoryMovement.getUpdatedAt());
    }

    @Test
    void givenAValidValues_whenCallWith_shouldReturnInventoryMovement() {
        final var aInventoryMovement = Fixture.InventoriesMovements.out();

        final var aNewInventoryMovement = InventoryMovement.with(
                aInventoryMovement.getId().getValue(),
                aInventoryMovement.getInventoryId().getValue(),
                aInventoryMovement.getSku(),
                aInventoryMovement.getQuantity(),
                aInventoryMovement.getStatus(),
                aInventoryMovement.getCreatedAt(),
                aInventoryMovement.getUpdatedAt()
        );

        Assertions.assertEquals(aInventoryMovement.getId(), aNewInventoryMovement.getId());
        Assertions.assertEquals(aInventoryMovement.getInventoryId(), aNewInventoryMovement.getInventoryId());
        Assertions.assertEquals(aInventoryMovement.getSku(), aNewInventoryMovement.getSku());
        Assertions.assertEquals(aInventoryMovement.getQuantity(), aNewInventoryMovement.getQuantity());
        Assertions.assertEquals(aInventoryMovement.getStatus(), aNewInventoryMovement.getStatus());
        Assertions.assertEquals(aInventoryMovement.getCreatedAt(), aNewInventoryMovement.getCreatedAt());
        Assertions.assertEquals(aInventoryMovement.getUpdatedAt(), aNewInventoryMovement.getUpdatedAt());
    }
}
