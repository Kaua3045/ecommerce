package com.kaua.ecommerce.infrastructure.inventory;

import com.kaua.ecommerce.domain.inventory.Inventory;
import com.kaua.ecommerce.domain.product.ProductID;
import com.kaua.ecommerce.infrastructure.DatabaseGatewayTest;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntity;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

@DatabaseGatewayTest
public class InventoryGatewayTest {

    @Autowired
    private InventoryMySQLGateway inventoryGateway;

    @Autowired
    private InventoryJpaRepository inventoryRepository;

    @Test
    void givenAValidInventories_whenCallCreateInBatch_shouldPersistInventories() {
        final var aProductId = ProductID.unique().getValue();
        final var aSkuOne = "sku-one";
        final var aQuantityOne = 10;
        final var aSkuTwo = "sku-two";
        final var aQuantityTwo = 20;

        final var aInventoryOne = Inventory.newInventory(aProductId, aSkuOne, aQuantityOne);
        final var aInventoryTwo = Inventory.newInventory(aProductId, aSkuTwo, aQuantityTwo);

        Assertions.assertEquals(0, this.inventoryRepository.count());

        this.inventoryGateway.createInBatch(Set.of(aInventoryOne, aInventoryTwo));

        Assertions.assertEquals(2, this.inventoryRepository.count());

        final var aPersistedInventoryOne = this.inventoryRepository.findById(aInventoryOne.getId().getValue()).get();

        Assertions.assertEquals(aInventoryOne.getId().getValue(), aPersistedInventoryOne.getId());
        Assertions.assertEquals(aInventoryOne.getProductId(), aPersistedInventoryOne.getProductId());
        Assertions.assertEquals(aInventoryOne.getSku(), aPersistedInventoryOne.getSku());
        Assertions.assertEquals(aInventoryOne.getQuantity(), aPersistedInventoryOne.getQuantity());
        Assertions.assertEquals(aInventoryOne.getCreatedAt(), aPersistedInventoryOne.getCreatedAt());
        Assertions.assertEquals(aInventoryOne.getUpdatedAt(), aPersistedInventoryOne.getUpdatedAt());

        final var aPersistedInventoryTwo = this.inventoryRepository.findById(aInventoryTwo.getId().getValue()).get();

        Assertions.assertEquals(aInventoryTwo.getId().getValue(), aPersistedInventoryTwo.getId());
        Assertions.assertEquals(aInventoryTwo.getProductId(), aPersistedInventoryTwo.getProductId());
        Assertions.assertEquals(aInventoryTwo.getSku(), aPersistedInventoryTwo.getSku());
        Assertions.assertEquals(aInventoryTwo.getQuantity(), aPersistedInventoryTwo.getQuantity());
        Assertions.assertEquals(aInventoryTwo.getCreatedAt(), aPersistedInventoryTwo.getCreatedAt());
        Assertions.assertEquals(aInventoryTwo.getUpdatedAt(), aPersistedInventoryTwo.getUpdatedAt());
    }

    @Test
    void givenAValidSkus_whenCallExistsBySkus_shouldReturnSkusAlreadyExists() {
        final var aProductId = ProductID.unique().getValue();
        final var aSkuOne = "sku-one";
        final var aQuantityOne = 10;
        final var aSkuTwo = "sku-two";
        final var aQuantityTwo = 20;

        final var aInventoryOne = Inventory.newInventory(aProductId, aSkuOne, aQuantityOne);
        final var aInventoryTwo = Inventory.newInventory(aProductId, aSkuTwo, aQuantityTwo);

        System.out.println(inventoryRepository.count());

        this.inventoryRepository.saveAllAndFlush(Set.of(aInventoryOne, aInventoryTwo)
                .stream().map(InventoryJpaEntity::toEntity).toList());

        final var existsSkus = this.inventoryGateway.existsBySkus(List.of(aSkuOne, aSkuTwo));

        Assertions.assertEquals(2, existsSkus.size());
        Assertions.assertTrue(existsSkus.contains(aSkuOne));
        Assertions.assertTrue(existsSkus.contains(aSkuTwo));
    }

    @Test
    void givenAnInvalidSku_whenCallExistsBySkus_shouldReturnEmpty() {
        final var aSku = "sku";

        final var existsSkus = this.inventoryGateway.existsBySkus(List.of(aSku));

        Assertions.assertTrue(existsSkus.isEmpty());
    }

    @Test
    void givenAValidProductId_whenCallCleanByProductId_shouldBeOk() {
        final var aProductId = ProductID.unique().getValue();
        final var aSkuOne = "sku-one";
        final var aQuantityOne = 10;
        final var aSkuTwo = "sku-two";
        final var aQuantityTwo = 20;

        final var aInventoryOne = Inventory.newInventory(aProductId, aSkuOne, aQuantityOne);
        final var aInventoryTwo = Inventory.newInventory(aProductId, aSkuTwo, aQuantityTwo);

        this.inventoryRepository.saveAllAndFlush(Set.of(aInventoryOne, aInventoryTwo)
                .stream().map(InventoryJpaEntity::toEntity).toList());

        Assertions.assertEquals(2, this.inventoryRepository.count());

        this.inventoryGateway.cleanByProductId(aProductId);

        Assertions.assertEquals(0, this.inventoryRepository.count());
    }

    @Test
    void givenAnInvalidProductId_whenCallCleanByProductId_shouldBeOk() {
        final var aProductId = "1";
        final var aSku = "sku";
        final var aQuantity = 10;

        final var aInventory = Inventory.newInventory(ProductID.unique().getValue(), aSku, aQuantity);

        this.inventoryRepository.saveAndFlush(InventoryJpaEntity.toEntity(aInventory));

        Assertions.assertEquals(1, this.inventoryRepository.count());

        this.inventoryGateway.cleanByProductId(aProductId);

        Assertions.assertEquals(1, this.inventoryRepository.count());
    }

    @Test
    void givenAValidSku_whenCallDeleteBySku_shouldBeOk() {
        final var aProductId = ProductID.unique().getValue();
        final var aSku = "sku";
        final var aQuantity = 10;

        final var aInventory = Inventory.newInventory(aProductId, aSku, aQuantity);

        this.inventoryRepository.saveAndFlush(InventoryJpaEntity.toEntity(aInventory));

        Assertions.assertEquals(1, this.inventoryRepository.count());

        Assertions.assertDoesNotThrow(() -> this.inventoryGateway.deleteBySku(aSku));

        Assertions.assertEquals(0, this.inventoryRepository.count());
    }

    @Test
    void givenAnInvalidSku_whenCallDeleteBySku_shouldBeOk() {
        final var aSku = "sku";

        Assertions.assertEquals(0, this.inventoryRepository.count());

        Assertions.assertDoesNotThrow(() -> this.inventoryGateway.deleteBySku(aSku));

        Assertions.assertEquals(0, this.inventoryRepository.count());
    }
}
