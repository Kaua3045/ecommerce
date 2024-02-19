package com.kaua.ecommerce.infrastructure.product.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaua.ecommerce.application.usecases.product.attributes.add.AddProductAttributesCommandParams;

import java.util.List;

public record AddProductAttributesInput(
        @JsonProperty("attributes") List<AddProductAttributesParamInput> attributesParams
) {

    public List<AddProductAttributesCommandParams> toCommandParams() {
        return attributesParams.stream()
                .map(it -> new AddProductAttributesCommandParams(
                        it.colorName(),
                        it.sizeName(),
                        it.weight(),
                        it.height(),
                        it.width(),
                        it.depth()))
                .toList();
    }
}
