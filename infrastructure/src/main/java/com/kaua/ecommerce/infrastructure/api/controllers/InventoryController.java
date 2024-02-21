package com.kaua.ecommerce.infrastructure.api.controllers;

import com.kaua.ecommerce.application.usecases.inventory.create.commands.CreateInventoryCommand;
import com.kaua.ecommerce.application.usecases.inventory.create.CreateInventoryUseCase;
import com.kaua.ecommerce.domain.inventory.Inventory;
import com.kaua.ecommerce.infrastructure.api.InventoryAPI;
import com.kaua.ecommerce.infrastructure.inventory.models.CreateInventoryInput;
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

    public InventoryController(final CreateInventoryUseCase createInventoryUseCase) {
        this.createInventoryUseCase = createInventoryUseCase;
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
}
