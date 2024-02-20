package com.kaua.ecommerce.infrastructure.inventory;

import com.kaua.ecommerce.domain.inventory.Inventory;
import com.kaua.ecommerce.domain.product.ProductID;
import com.kaua.ecommerce.infrastructure.DatabaseGatewayTest;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DatabaseGatewayTest
public class InventoryGatewayTest {

    @Autowired
    private InventoryMySQLGateway inventoryGateway;

    @Autowired
    private InventoryJpaRepository inventoryRepository;

    @Test
    void givenAValidInventory_whenCallCreate_shouldPersistInventory() {
        final var aProductId = ProductID.unique().getValue();
        final var aSku = "sku";
        final var aQuantity = 10;

        final var aInventory = Inventory.newInventory(aProductId, aSku, aQuantity);

        Assertions.assertEquals(0, this.inventoryRepository.count());

        final var createdInventory = this.inventoryGateway.create(aInventory);

        Assertions.assertEquals(1, this.inventoryRepository.count());

        final var aPersistedInventory = this.inventoryRepository.findById(createdInventory.getId().getValue()).get();

        Assertions.assertEquals(aInventory.getId().getValue(), aPersistedInventory.getId());
        Assertions.assertEquals(aInventory.getProductId(), aPersistedInventory.getProductId());
        Assertions.assertEquals(aInventory.getSku(), aPersistedInventory.getSku());
        Assertions.assertEquals(aInventory.getQuantity(), aPersistedInventory.getQuantity());
        Assertions.assertEquals(aInventory.getCreatedAt(), aPersistedInventory.getCreatedAt());
        Assertions.assertEquals(aInventory.getUpdatedAt(), aPersistedInventory.getUpdatedAt());
    }

    @Test
    void givenAValidSku_whenCallExistsBySku_shouldReturnTrue() {
        final var aProductId = ProductID.unique().getValue();
        final var aSku = "sku";
        final var aQuantity = 10;

        final var aInventory = Inventory.newInventory(aProductId, aSku, aQuantity);

        this.inventoryGateway.create(aInventory);

        final var exists = this.inventoryGateway.existsBySku(aSku);

        Assertions.assertTrue(exists);
    }

    @Test
    void givenAnInvalidSku_whenCallExistsBySku_shouldReturnFalse() {
        final var aSku = "sku";

        final var exists = this.inventoryGateway.existsBySku(aSku);

        Assertions.assertFalse(exists);
    }
}
