package com.kaua.ecommerce.application.usecases.product.media.remove;

public record RemoveProductImageCommand(
        String productId,
        String location
) {

    public static RemoveProductImageCommand with(final String productId, final String location) {
        return new RemoveProductImageCommand(productId, location);
    }
}
