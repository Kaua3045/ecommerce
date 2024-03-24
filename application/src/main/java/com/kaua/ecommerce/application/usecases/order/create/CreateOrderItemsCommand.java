package com.kaua.ecommerce.application.usecases.order.create;

public record CreateOrderItemsCommand(
        String productId,
        String sku,
        int quantity
) {

    public static CreateOrderItemsCommand with(
            final String productId,
            final String sku,
            final int quantity
    ) {
        return new CreateOrderItemsCommand(
                productId,
                sku,
                quantity
        );
    }
}
