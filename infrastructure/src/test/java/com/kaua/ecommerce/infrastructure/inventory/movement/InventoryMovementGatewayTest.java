package com.kaua.ecommerce.infrastructure.inventory.movement;

import com.kaua.ecommerce.domain.inventory.InventoryID;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovement;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovementStatus;
import com.kaua.ecommerce.infrastructure.DatabaseGatewayTest;
import com.kaua.ecommerce.infrastructure.inventory.movement.persistence.InventoryMovementJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@DatabaseGatewayTest
public class InventoryMovementGatewayTest {

    @Autowired
    private InventoryMovementMySQLGateway inventoryMovementGateway;

    @Autowired
    private InventoryMovementJpaRepository inventoryMovementRepository;

    @Test
    void givenAValidInventoriesMovements_whenCallCreateInBatch_shouldPersistInventoriesMovements() {
        final var aInventoryId = InventoryID.unique();
        final var aSkuOne = "sku-one";
        final var aQuantityOne = 10;
        final var aStatusOne = InventoryMovementStatus.IN;
        final var aSkuTwo = "sku-two";
        final var aQuantityTwo = 20;
        final var aStatusTwo = InventoryMovementStatus.IN;

        final var aInventoryMovementOne = InventoryMovement.newInventoryMovement(
                aInventoryId, aSkuOne, aQuantityOne, aStatusOne
        );

        final var aInventoryMovementTwo = InventoryMovement.newInventoryMovement(
                aInventoryId, aSkuTwo, aQuantityTwo, aStatusTwo
        );

        Assertions.assertEquals(0, this.inventoryMovementRepository.count());

        this.inventoryMovementGateway.createInBatch(Set.of(aInventoryMovementOne, aInventoryMovementTwo));

        Assertions.assertEquals(2, this.inventoryMovementRepository.count());

        final var aPersistedInventoryMovementOne = this.inventoryMovementRepository
                .findById(aInventoryMovementOne.getId().getValue()).get();

        Assertions.assertEquals(aInventoryMovementOne.getId().getValue(), aPersistedInventoryMovementOne.getId());
        Assertions.assertEquals(aInventoryMovementOne.getInventoryId().getValue(), aPersistedInventoryMovementOne.getInventoryId());
        Assertions.assertEquals(aInventoryMovementOne.getSku(), aPersistedInventoryMovementOne.getSku());
        Assertions.assertEquals(aInventoryMovementOne.getQuantity(), aPersistedInventoryMovementOne.getQuantity());
        Assertions.assertEquals(aInventoryMovementOne.getStatus(), aPersistedInventoryMovementOne.getMovementType());
        Assertions.assertEquals(aInventoryMovementOne.getCreatedAt(), aPersistedInventoryMovementOne.getCreatedAt());
        Assertions.assertEquals(aInventoryMovementOne.getUpdatedAt(), aPersistedInventoryMovementOne.getUpdatedAt());

        final var aPersistedInventoryMovementTwo = this.inventoryMovementRepository
                .findById(aInventoryMovementTwo.getId().getValue()).get();

        Assertions.assertEquals(aInventoryMovementTwo.getId().getValue(), aPersistedInventoryMovementTwo.getId());
        Assertions.assertEquals(aInventoryMovementTwo.getInventoryId().getValue(), aPersistedInventoryMovementTwo.getInventoryId());
        Assertions.assertEquals(aInventoryMovementTwo.getSku(), aPersistedInventoryMovementTwo.getSku());
        Assertions.assertEquals(aInventoryMovementTwo.getQuantity(), aPersistedInventoryMovementTwo.getQuantity());
        Assertions.assertEquals(aInventoryMovementTwo.getStatus(), aPersistedInventoryMovementTwo.getMovementType());
        Assertions.assertEquals(aInventoryMovementTwo.getCreatedAt(), aPersistedInventoryMovementTwo.getCreatedAt());
        Assertions.assertEquals(aInventoryMovementTwo.getUpdatedAt(), aPersistedInventoryMovementTwo.getUpdatedAt());
    }

    @Test
    void givenAValidInventoryMovement_whenCallCreate_shouldPersistInventoryMovement() {
        final var aInventoryId = InventoryID.unique();
        final var aSku = "sku-one";
        final var aQuantity = 10;
        final var aStatus = InventoryMovementStatus.IN;

        final var aInventoryMovement = InventoryMovement.newInventoryMovement(
                aInventoryId, aSku, aQuantity, aStatus
        );

        Assertions.assertEquals(0, this.inventoryMovementRepository.count());

        final var aResult = this.inventoryMovementGateway.create(aInventoryMovement);

        Assertions.assertEquals(1, this.inventoryMovementRepository.count());

        Assertions.assertEquals(aInventoryMovement.getId().getValue(), aResult.getId().getValue());
        Assertions.assertEquals(aInventoryMovement.getInventoryId().getValue(), aResult.getInventoryId().getValue());
        Assertions.assertEquals(aInventoryMovement.getSku(), aResult.getSku());
        Assertions.assertEquals(aInventoryMovement.getQuantity(), aResult.getQuantity());
        Assertions.assertEquals(aInventoryMovement.getStatus(), aResult.getStatus());
        Assertions.assertEquals(aInventoryMovement.getCreatedAt(), aResult.getCreatedAt());
        Assertions.assertEquals(aInventoryMovement.getUpdatedAt(), aResult.getUpdatedAt());
    }
}
