package com.kaua.ecommerce.infrastructure.inventory.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record GetInventoryResponse(
        @JsonProperty("id") String id,
        @JsonProperty("product_id") String productId,
        @JsonProperty("sku") String sku,
        @JsonProperty("quantity") int quantity,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,
        @JsonProperty("version") long version
) {
}
