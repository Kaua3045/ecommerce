package com.kaua.ecommerce.infrastructure.category.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Set;

public record ListCategoriesResponse(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("slug") String slug,
        @JsonProperty("parent_id") String parentId,
        @JsonProperty("sub_categories") Set<ListCategoriesResponse> subCategories,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt
) {
}
