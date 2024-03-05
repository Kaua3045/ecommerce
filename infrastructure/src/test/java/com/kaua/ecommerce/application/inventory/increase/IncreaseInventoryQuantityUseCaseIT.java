package com.kaua.ecommerce.application.inventory.increase;

import com.kaua.ecommerce.application.usecases.inventory.increase.IncreaseInventoryQuantityCommand;
import com.kaua.ecommerce.application.usecases.inventory.increase.IncreaseInventoryQuantityUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntity;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class IncreaseInventoryQuantityUseCaseIT {

    @Autowired
    private IncreaseInventoryQuantityUseCase increaseInventoryQuantityUseCase;

    @Autowired
    private InventoryJpaEntityRepository inventoryJpaEntityRepository;

    @Test
    void givenAValidCommand_whenIncreaseInventoryQuantity_shouldIncreaseInventoryQuantity() {
        final var aInventory = Fixture.Inventories.tshirtInventory();
        final var aOldInventoryQuantity = aInventory.getQuantity();

        final var aSku = aInventory.getSku();
        final var aQuantity = 10;

        final var aCommand = IncreaseInventoryQuantityCommand.with(aSku, aQuantity);

        this.inventoryJpaEntityRepository.save(InventoryJpaEntity.toEntity(aInventory));

        Assertions.assertEquals(1, this.inventoryJpaEntityRepository.count());

        final var aOutput = this.increaseInventoryQuantityUseCase.execute(aCommand).getRight();

        Assertions.assertEquals(aInventory.getId().getValue(), aOutput.inventoryId());
        Assertions.assertEquals(aInventory.getProductId(), aOutput.productId());
        Assertions.assertEquals(aSku, aOutput.sku());

        final var aPersistedInventory = this.inventoryJpaEntityRepository.findBySku(aSku).get();

        Assertions.assertEquals(aOldInventoryQuantity + aQuantity, aPersistedInventory.getQuantity());
    }

    @Test
    void givenAnInvalidQuantity_whenIncreaseInventoryQuantity_shouldReturnDomainException() {
        final var aInventory = Fixture.Inventories.tshirtInventory();

        final var aSku = aInventory.getSku();
        final var aQuantity = 0;

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("quantity", 0);

        final var aCommand = IncreaseInventoryQuantityCommand.with(aSku, aQuantity);

        this.inventoryJpaEntityRepository.save(InventoryJpaEntity.toEntity(aInventory));

        Assertions.assertEquals(1, this.inventoryJpaEntityRepository.count());

        final var aOutput = this.increaseInventoryQuantityUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());
    }
}
