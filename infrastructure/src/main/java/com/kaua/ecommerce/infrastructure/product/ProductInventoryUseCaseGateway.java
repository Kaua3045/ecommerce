package com.kaua.ecommerce.infrastructure.product;

import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.gateways.ProductInventoryGateway;
import com.kaua.ecommerce.application.usecases.inventory.create.CreateInventoryUseCase;
import com.kaua.ecommerce.application.usecases.inventory.create.commands.CreateInventoryCommand;
import com.kaua.ecommerce.application.usecases.inventory.create.commands.CreateInventoryCommandParams;
import com.kaua.ecommerce.application.usecases.inventory.delete.clean.CleanInventoriesByProductIdUseCase;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ProductInventoryUseCaseGateway implements ProductInventoryGateway {

    private static final Logger log = LoggerFactory.getLogger(ProductInventoryUseCaseGateway.class);

    private final CreateInventoryUseCase createInventoryUseCase;
    private final CleanInventoriesByProductIdUseCase cleanInventoriesByProductIdUseCase;

    public ProductInventoryUseCaseGateway(
            final CreateInventoryUseCase createInventoryUseCase,
            final CleanInventoriesByProductIdUseCase cleanInventoriesByProductIdUseCase
    ) {
        this.createInventoryUseCase = Objects.requireNonNull(createInventoryUseCase);
        this.cleanInventoriesByProductIdUseCase = Objects.requireNonNull(cleanInventoriesByProductIdUseCase);
    }

    @Override
    public Either<NotificationHandler, Void> createInventory(
            final String productId,
            final List<CreateInventoryParams> inventoryParams
    ) {
        final var aCommandInventoryParams = inventoryParams
                .stream()
                .map(it -> CreateInventoryCommandParams.with(it.sku(), it.quantity()))
                .collect(Collectors.toSet());

        final var aCommand = CreateInventoryCommand.with(productId, aCommandInventoryParams);

        final var aCreateInventoriesResult = this.createInventoryUseCase.execute(aCommand);

        if (aCreateInventoriesResult.isLeft()) {
            log.error("error on create inventories: {}", aCreateInventoriesResult.getLeft());
            return Either.left(aCreateInventoriesResult.getLeft());
        }

        log.info("inventories created: {}", aCreateInventoriesResult.getRight());

        return Either.right(null);
    }

    @Override
    public Either<NotificationHandler, Void> cleanInventoriesByProductId(String productId) {
        try {
            this.cleanInventoriesByProductIdUseCase.execute(productId);
            return Either.right(null);
        } catch (Exception e) {
            log.error("error on clean inventories by product id", e);
            return Either.left(NotificationHandler.create()
                    .append(new Error("error on clean inventories by product id")));
        }
    }
}
