package com.kaua.ecommerce.infrastructure.product.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaua.ecommerce.application.usecases.product.search.retrieve.list.ListProductsImagesOutput;

import java.math.BigDecimal;
import java.time.Instant;

public record ListProductsResponse(
        @JsonProperty("product_id") String productId,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("price") BigDecimal price,
        @JsonProperty("quantity") int quantity,
        @JsonProperty("category_id") String categoryId,
        @JsonProperty("banner_image") ListProductsImagesOutput bannerImage,
        @JsonProperty("status") String status,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,
        @JsonProperty("version") long version
) {
}
