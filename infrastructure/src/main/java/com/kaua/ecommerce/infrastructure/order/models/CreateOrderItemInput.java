package com.kaua.ecommerce.infrastructure.order.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaua.ecommerce.application.usecases.order.create.CreateOrderItemsCommand;

public record CreateOrderItemInput(
        @JsonProperty("product_id") String productId,
        @JsonProperty("sku") String sku,
        @JsonProperty("quantity") int quantity
) {

    public CreateOrderItemsCommand toCommand() {
        return CreateOrderItemsCommand.with(productId, sku, quantity);
    }
}
