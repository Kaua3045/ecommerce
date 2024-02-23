package com.kaua.ecommerce.infrastructure.product;

import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.gateways.ProductInventoryGateway;
import com.kaua.ecommerce.application.usecases.inventory.create.CreateInventoryUseCase;
import com.kaua.ecommerce.application.usecases.inventory.create.commands.CreateInventoryCommand;
import com.kaua.ecommerce.application.usecases.inventory.create.commands.CreateInventoryCommandParams;
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

    public ProductInventoryUseCaseGateway(final CreateInventoryUseCase createInventoryUseCase) {
        this.createInventoryUseCase = Objects.requireNonNull(createInventoryUseCase);
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
            return Either.left(aCreateInventoriesResult.getLeft());
        }

        log.info("inventories created: {}", aCreateInventoriesResult.getRight());

        return Either.right(null);
    }
}
