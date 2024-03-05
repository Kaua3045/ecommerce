package com.kaua.ecommerce.infrastructure.api.controllers;

import com.kaua.ecommerce.application.usecases.inventory.create.CreateInventoryUseCase;
import com.kaua.ecommerce.application.usecases.inventory.create.commands.CreateInventoryCommand;
import com.kaua.ecommerce.application.usecases.inventory.delete.clean.CleanInventoriesByProductIdUseCase;
import com.kaua.ecommerce.application.usecases.inventory.delete.remove.RemoveInventoryBySkuUseCase;
import com.kaua.ecommerce.application.usecases.inventory.increase.IncreaseInventoryQuantityCommand;
import com.kaua.ecommerce.application.usecases.inventory.increase.IncreaseInventoryQuantityUseCase;
import com.kaua.ecommerce.application.usecases.inventory.rollback.RollbackInventoryBySkuCommand;
import com.kaua.ecommerce.application.usecases.inventory.rollback.RollbackInventoryBySkuUseCase;
import com.kaua.ecommerce.domain.inventory.Inventory;
import com.kaua.ecommerce.infrastructure.api.InventoryAPI;
import com.kaua.ecommerce.infrastructure.inventory.models.CreateInventoryInput;
import com.kaua.ecommerce.infrastructure.inventory.models.IncreaseInventoryQuantityInput;
import com.kaua.ecommerce.infrastructure.utils.LogControllerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InventoryController implements InventoryAPI {

    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

    private final CreateInventoryUseCase createInventoryUseCase;
    private final CleanInventoriesByProductIdUseCase cleanInventoriesByProductIdUseCase;
    private final RemoveInventoryBySkuUseCase removeInventoryBySkuUseCase;
    private final RollbackInventoryBySkuUseCase rollbackInventoryBySkuUseCase;
    private final IncreaseInventoryQuantityUseCase increaseInventoryQuantityUseCase;

    public InventoryController(
            final CreateInventoryUseCase createInventoryUseCase,
            final CleanInventoriesByProductIdUseCase cleanInventoriesByProductIdUseCase,
            final RemoveInventoryBySkuUseCase removeInventoryBySkuUseCase,
            final RollbackInventoryBySkuUseCase rollbackInventoryBySkuUseCase,
            final IncreaseInventoryQuantityUseCase increaseInventoryQuantityUseCase
    ) {
        this.createInventoryUseCase = createInventoryUseCase;
        this.cleanInventoriesByProductIdUseCase = cleanInventoriesByProductIdUseCase;
        this.removeInventoryBySkuUseCase = removeInventoryBySkuUseCase;
        this.rollbackInventoryBySkuUseCase = rollbackInventoryBySkuUseCase;
        this.increaseInventoryQuantityUseCase = increaseInventoryQuantityUseCase;
    }

    @Override
    public ResponseEntity<?> createInventory(CreateInventoryInput body) {
        final var aResult = this.createInventoryUseCase.execute(CreateInventoryCommand.with(
                body.productId(),
                body.toCommandInventoryParams()
        ));

        LogControllerResult.logResult(
                log,
                Inventory.class,
                "createInventory",
                aResult
        );

        return aResult.isLeft()
                ? ResponseEntity.unprocessableEntity().body(aResult.getLeft())
                : ResponseEntity.status(HttpStatus.CREATED).body(aResult.getRight());
    }

    @Override
    public void rollbackInventoryBySkuAndProductId(String productId, String sku) {
        this.rollbackInventoryBySkuUseCase.execute(RollbackInventoryBySkuCommand.with(productId, sku));
        LogControllerResult.logResult(
                log,
                Inventory.class,
                "rollbackInventoryBySku",
                productId + " " + sku + " rolled back"
        );
    }

    @Override
    public ResponseEntity<?> increaseInventoryQuantityBySku(String sku, IncreaseInventoryQuantityInput body) {
        final var aCommand = IncreaseInventoryQuantityCommand.with(sku, body.quantity());

        final var aResult = this.increaseInventoryQuantityUseCase.execute(aCommand);

        LogControllerResult.logResult(
                log,
                Inventory.class,
                "increaseInventoryQuantityBySku",
                aResult
        );

        return aResult.isLeft()
                ? ResponseEntity.unprocessableEntity().body(aResult.getLeft())
                : ResponseEntity.ok(aResult.getRight());
    }

    @Override
    public void deleteInventoriesByProductId(String productId) {
        this.cleanInventoriesByProductIdUseCase.execute(productId);
        LogControllerResult.logResult(
                log,
                Inventory.class,
                "deleteInventoriesByProductId",
                productId + " deleted inventories"
        );
    }

    @Override
    public void deleteInventoryBySku(String sku) {
        this.removeInventoryBySkuUseCase.execute(sku);
        LogControllerResult.logResult(
                log,
                Inventory.class,
                "deleteInventoryBySku",
                sku + " deleted inventory"
        );
    }
}
