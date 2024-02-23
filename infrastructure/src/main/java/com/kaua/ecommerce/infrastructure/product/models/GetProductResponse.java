package com.kaua.ecommerce.infrastructure.product.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaua.ecommerce.application.usecases.product.retrieve.get.GetProductByIdOutputAttributes;
import com.kaua.ecommerce.application.usecases.product.retrieve.get.GetProductByIdOutputImages;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public record GetProductResponse(
        @JsonProperty("product_id") String productId,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("price") BigDecimal price,
        @JsonProperty("category_id") String categoryId,
        @JsonProperty("banner_image") GetProductByIdOutputImages bannerImage,
        @JsonProperty("gallery_images") Set<GetProductByIdOutputImages> galleryImages,
        @JsonProperty("attributes") Set<GetProductByIdOutputAttributes> attributes,
        @JsonProperty("status") String status,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,
        @JsonProperty("version") long version
) {
}
