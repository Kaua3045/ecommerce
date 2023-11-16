package com.kaua.ecommerce.infrastructure.category.models;

public record UpdateCategoryInput(
        String name,
        String description
) {
}
