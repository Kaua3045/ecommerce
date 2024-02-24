package com.kaua.ecommerce.application.gateways;

import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

import java.util.List;

public interface ProductInventoryGateway {

    Either<NotificationHandler, Void> createInventory(String productId, List<CreateInventoryParams> inventoryParams);

    Either<NotificationHandler, Void> cleanInventoriesByProductId(String productId);

    record CreateInventoryParams(String sku, int quantity) {}
}
