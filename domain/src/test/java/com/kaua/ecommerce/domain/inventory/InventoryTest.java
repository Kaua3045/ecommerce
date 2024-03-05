package com.kaua.ecommerce.domain.inventory;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.UnitTest;
import com.kaua.ecommerce.domain.event.EventsTypes;
import com.kaua.ecommerce.domain.inventory.events.InventoryCreatedRollbackBySkusEvent;
import com.kaua.ecommerce.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class InventoryTest extends UnitTest {

    @Test
    void givenAValidValues_whenCallNewInventory_thenInventoryIsCreated() {
        // Given
        final var productId = "product-id";
        final var sku = "sku";
        final var quantity = 10;

        // When
        final var inventory = Inventory.newInventory(productId, sku, quantity);

        // Then
        Assertions.assertEquals(productId, inventory.getProductId());
        Assertions.assertEquals(sku, inventory.getSku());
        Assertions.assertEquals(quantity, inventory.getQuantity());
        Assertions.assertNotNull(inventory.getCreatedAt());
        Assertions.assertNotNull(inventory.getUpdatedAt());
        Assertions.assertDoesNotThrow(() -> inventory.validate(new ThrowsValidationHandler()));
    }

    @Test
    void testInventoryIdEqualsAndHashCode() {
        final var aInventoryId = InventoryID.from("123456789");
        final var anotherInventoryId = InventoryID.from("123456789");

        Assertions.assertTrue(aInventoryId.equals(anotherInventoryId));
        Assertions.assertTrue(aInventoryId.equals(aInventoryId));
        Assertions.assertFalse(aInventoryId.equals(null));
        Assertions.assertFalse(aInventoryId.equals(""));
        Assertions.assertEquals(aInventoryId.hashCode(), anotherInventoryId.hashCode());
    }

    @Test
    void givenAValidValues_whenCallWith_shouldReturnInventory() {
        final var aInventory = Fixture.Inventories.tshirtInventory();

        final var aInventoryWith = Inventory.with(
                aInventory.getId().getValue(),
                aInventory.getProductId(),
                aInventory.getSku(),
                aInventory.getQuantity(),
                aInventory.getCreatedAt(),
                aInventory.getUpdatedAt(),
                aInventory.getVersion()
        );

        Assertions.assertEquals(aInventory.getId(), aInventoryWith.getId());
        Assertions.assertEquals(aInventory.getProductId(), aInventoryWith.getProductId());
        Assertions.assertEquals(aInventory.getSku(), aInventoryWith.getSku());
        Assertions.assertEquals(aInventory.getQuantity(), aInventoryWith.getQuantity());
        Assertions.assertEquals(aInventory.getCreatedAt(), aInventoryWith.getCreatedAt());
        Assertions.assertEquals(aInventory.getUpdatedAt(), aInventoryWith.getUpdatedAt());
        Assertions.assertEquals(aInventory.getVersion(), aInventoryWith.getVersion());
    }

    @Test
    void givenAValidInventory_whenCallWith_shouldReturnInventory() {
        final var aInventory = Fixture.Inventories.tshirtInventory();

        final var aInventoryWith = Inventory.with(aInventory);

        Assertions.assertEquals(aInventory.getId(), aInventoryWith.getId());
        Assertions.assertEquals(aInventory.getProductId(), aInventoryWith.getProductId());
        Assertions.assertEquals(aInventory.getSku(), aInventoryWith.getSku());
        Assertions.assertEquals(aInventory.getQuantity(), aInventoryWith.getQuantity());
        Assertions.assertEquals(aInventory.getCreatedAt(), aInventoryWith.getCreatedAt());
        Assertions.assertEquals(aInventory.getUpdatedAt(), aInventoryWith.getUpdatedAt());
    }

    @Test
    void givenAValidInventory_whenCallToString_shouldReturnString() {
        final var aInventory = Fixture.Inventories.tshirtInventory();

        final var aInventoryString = aInventory.toString();

        Assertions.assertTrue(aInventoryString.contains(aInventory.getId().getValue()));
        Assertions.assertTrue(aInventoryString.contains(aInventory.getProductId()));
        Assertions.assertTrue(aInventoryString.contains(aInventory.getSku()));
        Assertions.assertTrue(aInventoryString.contains(String.valueOf(aInventory.getQuantity())));
        Assertions.assertTrue(aInventoryString.contains(aInventory.getCreatedAt().toString()));
        Assertions.assertTrue(aInventoryString.contains(aInventory.getUpdatedAt().toString()));
        Assertions.assertTrue(aInventoryString.contains(String.valueOf(aInventory.getVersion())));
    }

    @Test
    void givenAValidSkus_whenCallInventoryCreatedRollbackBySkusEventFrom_shouldReturnEvent() {
        final var skus = Fixture.createSku("tshirt");

        final var aEvent = InventoryCreatedRollbackBySkusEvent.from(List.of(skus));

        Assertions.assertEquals(1, aEvent.skus().size());
        Assertions.assertEquals(Inventory.class.getSimpleName().toLowerCase(), aEvent.aggregateName());
        Assertions.assertEquals(EventsTypes.INVENTORY_CREATED_ROLLBACK_BY_SKUS, aEvent.eventType());
        Assertions.assertNotNull(aEvent.occurredOn());
        Assertions.assertEquals(0, aEvent.version());
    }

    @Test
    void givenAValidQuantity_whenCallIncreaseQuantity_shouldReturnInventoryUpdated() {
        final var aInventory = Fixture.Inventories.tshirtInventory();

        final var expectedQuantity = aInventory.getQuantity() + 10;

        final var aInventoryUpdatedAt = aInventory.getUpdatedAt();

        final var aInventoryUpdated = aInventory.increaseQuantity(10);

        Assertions.assertEquals(aInventory.getId(), aInventoryUpdated.getId());
        Assertions.assertEquals(aInventory.getProductId(), aInventoryUpdated.getProductId());
        Assertions.assertEquals(aInventory.getSku(), aInventoryUpdated.getSku());
        Assertions.assertEquals(expectedQuantity, aInventoryUpdated.getQuantity());
        Assertions.assertEquals(aInventory.getCreatedAt(), aInventoryUpdated.getCreatedAt());
        Assertions.assertTrue(aInventoryUpdated.getUpdatedAt().isAfter(aInventoryUpdatedAt));
    }
}
