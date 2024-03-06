package com.kaua.ecommerce.application.inventory.decrease;

import com.kaua.ecommerce.application.usecases.inventory.decrease.DecreaseInventoryQuantityCommand;
import com.kaua.ecommerce.application.usecases.inventory.decrease.DecreaseInventoryQuantityUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovementStatus;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.inventory.movement.persistence.InventoryMovementJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntity;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class DecreaseInventoryQuantityUseCaseIT {

    @Autowired
    private DecreaseInventoryQuantityUseCase decreaseInventoryQuantityUseCase;

    @Autowired
    private InventoryJpaEntityRepository inventoryJpaEntityRepository;

    @Autowired
    private InventoryMovementJpaEntityRepository inventoryMovementJpaRepository;

    @Test
    void givenAValidCommand_whenDecreaseInventoryQuantity_shouldDecreaseInventoryQuantity() {
        final var aInventory = Fixture.Inventories.tshirtInventory();
        final var aOldInventoryQuantity = aInventory.getQuantity();

        final var aSku = aInventory.getSku();
        final var aQuantity = 5;

        final var aCommand = DecreaseInventoryQuantityCommand.with(aSku, aQuantity);

        this.inventoryJpaEntityRepository.save(InventoryJpaEntity.toEntity(aInventory));

        Assertions.assertEquals(1, this.inventoryJpaEntityRepository.count());

        final var aOutput = this.decreaseInventoryQuantityUseCase.execute(aCommand).getRight();

        Assertions.assertEquals(aInventory.getId().getValue(), aOutput.inventoryId());
        Assertions.assertEquals(aInventory.getProductId(), aOutput.productId());
        Assertions.assertEquals(aSku, aOutput.sku());

        final var aPersistedInventory = this.inventoryJpaEntityRepository.findBySku(aSku).get();
        final var aPersistedInventoryMovements = this.inventoryMovementJpaRepository.findAll()
                .stream().findFirst().get();

        Assertions.assertEquals(aOldInventoryQuantity - aQuantity, aPersistedInventory.getQuantity());
        Assertions.assertEquals(aQuantity, aPersistedInventoryMovements.getQuantity());
        Assertions.assertEquals(InventoryMovementStatus.OUT, aPersistedInventoryMovements.getMovementType());
    }

    @Test
    void givenAnInvalidQuantity_whenCallDecreaseInventoryQuantity_shouldReturnDomainException() {
        final var aInventory = Fixture.Inventories.tshirtInventory();

        final var aSku = aInventory.getSku();
        final var aQuantity = 0;

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("quantity", 0);

        final var aCommand = DecreaseInventoryQuantityCommand.with(aSku, aQuantity);

        this.inventoryJpaEntityRepository.save(InventoryJpaEntity.toEntity(aInventory));

        Assertions.assertEquals(1, this.inventoryJpaEntityRepository.count());

        final var aOutput = this.decreaseInventoryQuantityUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());
    }
}
