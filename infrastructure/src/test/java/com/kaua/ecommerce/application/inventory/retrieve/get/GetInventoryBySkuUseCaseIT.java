package com.kaua.ecommerce.application.inventory.retrieve.get;

import com.kaua.ecommerce.application.usecases.inventory.retrieve.get.GetInventoryBySkuUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntity;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class GetInventoryBySkuUseCaseIT {

    @Autowired
    private InventoryJpaEntityRepository inventoryJpaEntityRepository;

    @Autowired
    private GetInventoryBySkuUseCase getInventoryBySkuUseCase;

    @Test
    void givenAValidSku_whenCallGetInventoryBySkuUseCase_thenReturnInventory() {
        final var aInventory = Fixture.Inventories.tshirtInventory();
        final var aSku = aInventory.getSku();

        inventoryJpaEntityRepository.save(InventoryJpaEntity.toEntity(aInventory));

        final var aOutput = this.getInventoryBySkuUseCase.execute(aSku);

        Assertions.assertEquals(aInventory.getId().getValue(), aOutput.id());
        Assertions.assertEquals(aInventory.getProductId(), aOutput.productId());
        Assertions.assertEquals(aInventory.getSku(), aOutput.sku());
        Assertions.assertEquals(aInventory.getQuantity(), aOutput.quantity());
        Assertions.assertEquals(aInventory.getCreatedAt(), aOutput.createdAt());
        Assertions.assertEquals(aInventory.getUpdatedAt(), aOutput.updatedAt());
        Assertions.assertEquals(aInventory.getVersion(), aOutput.version());
    }
}
