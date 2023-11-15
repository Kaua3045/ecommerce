package com.kaua.ecommerce.infrastructure.category.models;

public record CreateCategoryInput(
        String name,
        String description
) {
}
