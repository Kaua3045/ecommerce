package com.kaua.ecommerce.infrastructure.inventory.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IncreaseInventoryQuantityInput(
        @JsonProperty("quantity") int quantity
) {
}
