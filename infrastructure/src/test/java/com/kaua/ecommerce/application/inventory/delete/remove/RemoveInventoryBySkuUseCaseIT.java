package com.kaua.ecommerce.application.inventory.delete.remove;

import com.kaua.ecommerce.application.usecases.inventory.delete.remove.RemoveInventoryBySkuUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.inventory.movement.persistence.InventoryMovementJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntity;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class RemoveInventoryBySkuUseCaseIT {

    @Autowired
    private InventoryJpaEntityRepository inventoryRepository;

    @Autowired
    private InventoryMovementJpaEntityRepository inventoryMovementRepository;

    @Autowired
    private RemoveInventoryBySkuUseCase removeInventoryBySkuUseCase;

    @Test
    void givenAValidSku_whenCallRemoveInventoryBySku_shouldBeOk() {
        final var aProduct = Fixture.Products.book();
        final var aInventory = Fixture.Inventories.createInventoryByProduct(aProduct);
        final var aSku = aInventory.getSku();

        this.inventoryRepository.save(InventoryJpaEntity.toEntity(aInventory));

        this.inventoryRepository.save(InventoryJpaEntity.toEntity(Fixture.Inventories.tshirtInventory()));

        Assertions.assertEquals(2, this.inventoryRepository.count());

        Assertions.assertDoesNotThrow(() -> this.removeInventoryBySkuUseCase.execute(aSku));

        Assertions.assertEquals(1, this.inventoryRepository.count());
        Assertions.assertEquals(1, this.inventoryMovementRepository.count());

        Assertions.assertTrue(this.inventoryRepository.findById(aInventory.getId().getValue()).isEmpty());
    }

    @Test
    void givenAnInvalidSku_whenCallRemoveInventoryBySku_shouldBeOk() {
        final var aSku = Fixture.createSku("invalid-sku");
        final var aInventory = Fixture.Inventories.tshirtInventory();

        this.inventoryRepository.save(InventoryJpaEntity.toEntity(aInventory));

        Assertions.assertEquals(1, this.inventoryRepository.count());

        Assertions.assertDoesNotThrow(() -> this.removeInventoryBySkuUseCase.execute(aSku));

        Assertions.assertEquals(1, this.inventoryRepository.count());
    }
}
