package com.kaua.ecommerce.application.usecases.product.attributes.add;

import java.util.List;

public record AddProductAttributesCommand(
        String productId,
        List<AddProductAttributesCommandParams> attributesParams
) {

    public static AddProductAttributesCommand with(
            final String aProductId,
            final List<AddProductAttributesCommandParams> aAttributesParams
    ) {
        return new AddProductAttributesCommand(aProductId, aAttributesParams);
    }
}
