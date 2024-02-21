package com.kaua.ecommerce.infrastructure.inventory.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateInventoryInputParams(
        @JsonProperty("sku") String sku,
        @JsonProperty("quantity") int quantity
) {
}
