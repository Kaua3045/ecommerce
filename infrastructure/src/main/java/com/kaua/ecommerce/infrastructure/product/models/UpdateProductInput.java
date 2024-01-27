package com.kaua.ecommerce.infrastructure.product.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record UpdateProductInput(
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("price") BigDecimal price,
        @JsonProperty("quantity") int quantity,
        @JsonProperty("category_id") String categoryId
) {
}
