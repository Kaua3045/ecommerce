package com.kaua.ecommerce.infrastructure.product.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaua.ecommerce.application.usecases.product.create.CreateProductCommand;
import com.kaua.ecommerce.application.usecases.product.create.CreateProductCommandAttributes;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductInput(
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("price") BigDecimal price,
        @JsonProperty("quantity") int quantity,
        @JsonProperty("category_id") String categoryId,
        @JsonProperty("attributes") List<CreateProductInputAttributes> attributes
) {

    public CreateProductCommand toCommand() {
        return CreateProductCommand.with(
                name(),
                description(),
                price(),
                quantity(),
                categoryId(),
                attributes().stream().map(attribute -> CreateProductCommandAttributes.with(
                        attribute.colorName(),
                        attribute.sizeName(),
                        attribute.weight(),
                        attribute.height(),
                        attribute.width(),
                        attribute.depth()
                )).toList()
        );
    }
}
