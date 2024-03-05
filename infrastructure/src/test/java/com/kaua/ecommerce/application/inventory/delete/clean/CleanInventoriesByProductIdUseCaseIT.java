package com.kaua.ecommerce.application.inventory.delete.clean;

import com.kaua.ecommerce.application.usecases.inventory.delete.clean.CleanInventoriesByProductIdUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntity;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class CleanInventoriesByProductIdUseCaseIT {

    @Autowired
    private InventoryJpaEntityRepository inventoryRepository;

    @Autowired
    private CleanInventoriesByProductIdUseCase cleanInventoriesByProductIdUseCase;

    @Test
    void givenAValidProductId_whenCallCleanInventoriesByProductId_shouldBeOk() {
        final var aProduct = Fixture.Products.book();
        final var aProductId = aProduct.getId().getValue();
        final var aInventory = Fixture.Inventories.createInventoryByProduct(aProduct);

        this.inventoryRepository.save(InventoryJpaEntity.toEntity(aInventory));

        this.inventoryRepository.save(InventoryJpaEntity.toEntity(Fixture.Inventories.tshirtInventory()));

        Assertions.assertEquals(2, this.inventoryRepository.count());

        Assertions.assertDoesNotThrow(() -> this.cleanInventoriesByProductIdUseCase.execute(aProductId));

        Assertions.assertEquals(1, this.inventoryRepository.count());

        Assertions.assertTrue(this.inventoryRepository.findById(aInventory.getId().getValue()).isEmpty());
    }

    @Test
    void givenAnInvalidProductId_whenCallCleanInventoriesByProductId_shouldBeOk() {
        final var aProductId = "1";
        final var aInventory = Fixture.Inventories.tshirtInventory();

        this.inventoryRepository.save(InventoryJpaEntity.toEntity(aInventory));

        Assertions.assertEquals(1, this.inventoryRepository.count());

        Assertions.assertDoesNotThrow(() -> this.cleanInventoriesByProductIdUseCase.execute(aProductId));

        Assertions.assertEquals(1, this.inventoryRepository.count());
    }
}
