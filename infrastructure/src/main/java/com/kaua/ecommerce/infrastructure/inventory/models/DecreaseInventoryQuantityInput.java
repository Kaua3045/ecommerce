package com.kaua.ecommerce.infrastructure.inventory.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DecreaseInventoryQuantityInput(
        @JsonProperty("quantity") int quantity
) {
}
