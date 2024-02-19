package com.kaua.ecommerce.application.usecases.product.attributes.remove;

public record RemoveProductAttributesCommand(
        String productId,
        String sku
) {

    public static RemoveProductAttributesCommand with(final String productId, final String sku) {
        return new RemoveProductAttributesCommand(productId, sku);
    }
}
