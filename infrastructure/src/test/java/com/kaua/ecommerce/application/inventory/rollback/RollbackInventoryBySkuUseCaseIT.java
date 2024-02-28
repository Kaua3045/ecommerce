package com.kaua.ecommerce.application.inventory.rollback;

import com.kaua.ecommerce.application.usecases.inventory.rollback.RollbackInventoryBySkuCommand;
import com.kaua.ecommerce.application.usecases.inventory.rollback.RollbackInventoryBySkuUseCase;
import com.kaua.ecommerce.domain.inventory.InventoryID;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovement;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovementStatus;
import com.kaua.ecommerce.domain.product.ProductID;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.inventory.movement.persistence.InventoryMovementJpaEntity;
import com.kaua.ecommerce.infrastructure.inventory.movement.persistence.InventoryMovementJpaRepository;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@IntegrationTest
public class RollbackInventoryBySkuUseCaseIT {

    @Autowired
    private InventoryMovementJpaRepository inventoryMovementJpaRepository;

    @Autowired
    private InventoryJpaRepository inventoryJpaRepository;

    @Autowired
    private RollbackInventoryBySkuUseCase rollbackInventoryBySkuUseCase;

    @Test
    void givenAValidSkuAndProductId_whenRollbackInventoryBySku_shouldRollbackInventory() {
        final var aProductId = ProductID.unique().getValue();
        final var aSku = "123-tshirt-red-m";
        final var aQuantity = 10;

        this.inventoryMovementJpaRepository.save(InventoryMovementJpaEntity.toEntity(
                InventoryMovement.newInventoryMovement(
                        InventoryID.unique(),
                        aSku,
                        aQuantity,
                        InventoryMovementStatus.REMOVED
                )
        ));

        final var aCommand = RollbackInventoryBySkuCommand.with(aSku, aProductId);

        Assertions.assertEquals(1, this.inventoryMovementJpaRepository.count());

        Assertions.assertDoesNotThrow(() -> this.rollbackInventoryBySkuUseCase.execute(aCommand));

        Assertions.assertEquals(1, this.inventoryMovementJpaRepository.count());
        Assertions.assertEquals(1, this.inventoryJpaRepository.count());

        final var aInventory = this.inventoryJpaRepository.findBySku(aSku).get();

        Assertions.assertEquals(aProductId, aInventory.getProductId());
        Assertions.assertEquals(aSku, aInventory.getSku());
        Assertions.assertEquals(aQuantity, aInventory.getQuantity());

        final var aInventoryMovement = this.inventoryMovementJpaRepository.findAll()
                .stream().findFirst().get();

        Assertions.assertEquals(aInventory.getId(), aInventoryMovement.getInventoryId());
        Assertions.assertEquals(aSku, aInventoryMovement.getSku());
        Assertions.assertEquals(aQuantity, aInventoryMovement.getQuantity());
        Assertions.assertEquals(InventoryMovementStatus.IN, aInventoryMovement.getMovementType());
    }

    @Test
    void testConcurrencyOnCallRollbackInventoryBySkuUseCase() {
        final var aProductId = ProductID.unique().getValue();
        final var aSku = "123-tshirt-red-m";
        final var aQuantity = 10;

        this.inventoryMovementJpaRepository.save(InventoryMovementJpaEntity.toEntity(
                InventoryMovement.newInventoryMovement(
                        InventoryID.unique(),
                        aSku,
                        aQuantity,
                        InventoryMovementStatus.REMOVED
                )
        ));

        final var aCommand = RollbackInventoryBySkuCommand.with(aSku, aProductId);

        Assertions.assertEquals(1, this.inventoryMovementJpaRepository.count());

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            tasks.add(() -> {
                this.rollbackInventoryBySkuUseCase.execute(aCommand);
                return null;
            });
        }

        try {
            executorService.invokeAll(tasks);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }

        Assertions.assertEquals(1, this.inventoryMovementJpaRepository.count());
        Assertions.assertEquals(1, this.inventoryJpaRepository.count());

        final var aInventory = this.inventoryJpaRepository.findBySku(aSku).get();
        Assertions.assertEquals(aProductId, aInventory.getProductId());
        Assertions.assertEquals(aSku, aInventory.getSku());
        Assertions.assertEquals(aQuantity, aInventory.getQuantity());

        final var aInventoryMovement = this.inventoryMovementJpaRepository.findAll()
                .stream().findFirst().get();

        Assertions.assertEquals(aInventory.getId(), aInventoryMovement.getInventoryId());
        Assertions.assertEquals(aSku, aInventoryMovement.getSku());
        Assertions.assertEquals(aQuantity, aInventoryMovement.getQuantity());
        Assertions.assertEquals(InventoryMovementStatus.IN, aInventoryMovement.getMovementType());
    }
}
