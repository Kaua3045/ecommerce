package com.kaua.ecommerce.infrastructure.inventory;

import com.kaua.ecommerce.domain.inventory.Inventory;
import com.kaua.ecommerce.domain.pagination.SearchQuery;
import com.kaua.ecommerce.domain.product.ProductID;
import com.kaua.ecommerce.infrastructure.DatabaseGatewayTest;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntity;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntityRepository;
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
    private InventoryJpaEntityRepository inventoryRepository;

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

    @Test
    void givenAValidSku_whenCallFindBySku_shouldReturnInventory() {
        final var aProductId = ProductID.unique().getValue();
        final var aSku = "sku";
        final var aQuantity = 10;

        final var aInventory = Inventory.newInventory(aProductId, aSku, aQuantity);

        this.inventoryRepository.saveAndFlush(InventoryJpaEntity.toEntity(aInventory));

        final var aInventoryPersisted = this.inventoryGateway.findBySku(aSku).get();

        Assertions.assertEquals(aInventory.getId().getValue(), aInventoryPersisted.getId().getValue());
        Assertions.assertEquals(aInventory.getProductId(), aInventoryPersisted.getProductId());
        Assertions.assertEquals(aInventory.getSku(), aInventoryPersisted.getSku());
        Assertions.assertEquals(aInventory.getQuantity(), aInventoryPersisted.getQuantity());
        Assertions.assertEquals(aInventory.getCreatedAt(), aInventoryPersisted.getCreatedAt());
        Assertions.assertEquals(aInventory.getUpdatedAt(), aInventoryPersisted.getUpdatedAt());
    }

    @Test
    void givenAnInvalidSku_whenCallFindBySku_shouldReturnEmpty() {
        final var aSku = "sku";

        final var aInventory = this.inventoryGateway.findBySku(aSku);

        Assertions.assertTrue(aInventory.isEmpty());
    }

    @Test
    void givenAValidProductId_whenCallFindByProductId_shouldReturnInventories() {
        final var aProductId = ProductID.unique().getValue();
        final var aSkuOne = "sku-one";
        final var aQuantityOne = 10;
        final var aSkuTwo = "sku-two";
        final var aQuantityTwo = 20;

        final var aInventoryOne = Inventory.newInventory(aProductId, aSkuOne, aQuantityOne);
        final var aInventoryTwo = Inventory.newInventory(aProductId, aSkuTwo, aQuantityTwo);

        this.inventoryRepository.saveAllAndFlush(Set.of(aInventoryOne, aInventoryTwo)
                .stream().map(InventoryJpaEntity::toEntity).toList());

        final var aInventories = this.inventoryGateway.findByProductId(aProductId);

        Assertions.assertEquals(2, aInventories.size());
    }

    @Test
    void givenAnInvalidProductId_whenCallFindByProductId_shouldReturnEmpty() {
        final var aProductId = "1";
        final var aSku = "sku";
        final var aQuantity = 10;

        final var aInventory = Inventory.newInventory(ProductID.unique().getValue(), aSku, aQuantity);

        this.inventoryRepository.saveAndFlush(InventoryJpaEntity.toEntity(aInventory));

        final var aInventories = this.inventoryGateway.findByProductId(aProductId);

        Assertions.assertTrue(aInventories.isEmpty());
    }

    @Test
    void givenAValidInventory_whenCallUpdate_shouldUpdateInventory() {
        final var aProductId = ProductID.unique().getValue();
        final var aSku = "sku";
        final var aQuantity = 10;

        final var aInventory = Inventory.newInventory(aProductId, aSku, aQuantity);

        this.inventoryRepository.saveAndFlush(InventoryJpaEntity.toEntity(aInventory));

        final var aInventoryToUpdate = aInventory.increaseQuantity(5);

        final var aInventoryUpdated = this.inventoryGateway.update(aInventoryToUpdate);

        Assertions.assertEquals(aInventory.getId().getValue(), aInventoryUpdated.getId().getValue());
        Assertions.assertEquals(aInventory.getProductId(), aInventoryUpdated.getProductId());
        Assertions.assertEquals(aInventory.getSku(), aInventoryUpdated.getSku());
        Assertions.assertEquals(15, aInventoryUpdated.getQuantity());
        Assertions.assertNotNull(aInventoryUpdated.getCreatedAt());
        Assertions.assertNotNull(aInventoryUpdated.getUpdatedAt());
    }

    @Test
    void givenAValidProductIdAndQuery_whenCallFindAllByProductId_shouldReturnAPaginationOfInventories() {
        final var aInventory = Inventory.newInventory("1", "sku", 10);
        final var anotherInventory = Inventory.newInventory("1", "abablabla", 10);
        final var aInventories = List.of(aInventory, anotherInventory);

        this.inventoryRepository.saveAll(aInventories.stream().map(InventoryJpaEntity::toEntity).toList());

        final var aPage = 0;
        final var aPerPage = 1;
        final var aTotalPages = 2;
        final var aTotalItems = 2;

        Assertions.assertEquals(2, this.inventoryRepository.count());

        final var aQuery = new SearchQuery(0, 1, "", "sku", "ASC");
        final var actualResult = this.inventoryGateway.findAllByProductId(aQuery, aInventory.getProductId());

        Assertions.assertEquals(aPage, actualResult.currentPage());
        Assertions.assertEquals(aPerPage, actualResult.perPage());
        Assertions.assertEquals(aTotalPages, actualResult.totalPages());
        Assertions.assertEquals(aTotalItems, actualResult.totalItems());
        Assertions.assertEquals(aPerPage, actualResult.items().size());
        Assertions.assertEquals(anotherInventory.getSku(), actualResult.items().get(0).getSku());
    }

    @Test
    void givenAValidProductIdAndQueryButHasNoData_whenCallFindAllByProductId_shouldReturnEmptyInventories() {
        final var aPage = 0;
        final var aPerPage = 1;
        final var aTotalPages = 0;
        final var aTotalItems = 0;

        final var aQuery = new SearchQuery(aPage, aPerPage, "", "sku", "ASC");
        final var actualResult = this.inventoryGateway.findAllByProductId(aQuery, "1");

        Assertions.assertEquals(aPage, actualResult.currentPage());
        Assertions.assertEquals(aPerPage, actualResult.perPage());
        Assertions.assertEquals(aTotalPages, actualResult.totalPages());
        Assertions.assertEquals(aTotalItems, actualResult.totalItems());
        Assertions.assertEquals(0, actualResult.items().size());
    }

    @Test
    void givenAValidProductIdAndQueryAndTerms_whenCallFindAllByProductId_shouldReturnAPaginationOfInventories() {
        final var aInventory = Inventory.newInventory("1", "xpto", 10);
        final var aInventories = List.of(
                aInventory,
                Inventory.newInventory("1", "abablabla", 10));

        this.inventoryRepository.saveAll(aInventories.stream().map(InventoryJpaEntity::toEntity).toList());

        final var aPage = 0;
        final var aPerPage = 1;
        final var aTotalPages = 1;
        final var aTotalItems = 1;

        Assertions.assertEquals(2, this.inventoryRepository.count());

        final var aQuery = new SearchQuery(0, 1, "xp", "sku", "ASC");
        final var actualResult = this.inventoryGateway.findAllByProductId(aQuery, aInventory.getProductId());

        Assertions.assertEquals(aPage, actualResult.currentPage());
        Assertions.assertEquals(aPerPage, actualResult.perPage());
        Assertions.assertEquals(aTotalPages, actualResult.totalPages());
        Assertions.assertEquals(aTotalItems, actualResult.totalItems());
        Assertions.assertEquals(aPerPage, actualResult.items().size());
        Assertions.assertEquals(aInventory.getSku(), actualResult.items().get(0).getSku());
    }
}
