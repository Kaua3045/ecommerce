package com.kaua.ecommerce.infrastructure.product.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record CreateProductInput(
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("price") BigDecimal price,
        @JsonProperty("quantity") int quantity,
        @JsonProperty("category_id") String categoryId,
        @JsonProperty("color_name") String colorName,
        @JsonProperty("size_name") String sizeName,
        @JsonProperty("weight") double weight,
        @JsonProperty("height") double height,
        @JsonProperty("width") double width,
        @JsonProperty("depth") double depth
) {
}
