package com.kaua.ecommerce.infrastructure.inventory.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateInventoryInput(
        @JsonProperty("product_id") String productId,
        @JsonProperty("sku") String sku,
        @JsonProperty("quantity") int quantity
) {
}
