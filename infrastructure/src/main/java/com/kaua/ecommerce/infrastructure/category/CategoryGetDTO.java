package com.kaua.ecommerce.infrastructure.category;

import java.util.Set;
import java.util.stream.Collectors;

public record CategoryGetDTO(
        String id,
        String name,
        String description,
        Set<String> subCategoriesIds,
        boolean isPrimary
) {

    public static CategoryGetDTO from(final CategoryEntity categoryEntity) {
        return new CategoryGetDTO(
                categoryEntity.getId(),
                categoryEntity.getName(),
                categoryEntity.getDescription(),
                categoryEntity.getSubCategoriesIds(),
                categoryEntity.isPrimary()
        );
    }
}
