package com.kaua.ecommerce.infrastructure.category;

import java.util.Set;
import java.util.stream.Collectors;

public record CategoryGetDTO(
        String id,
        String name,
        String description,
        Set<CategoryGetDTO> subCategories
) {

    public static CategoryGetDTO from(final CategoryEntity categoryEntity) {
        return new CategoryGetDTO(
                categoryEntity.getId(),
                categoryEntity.getName(),
                categoryEntity.getDescription(),
                categoryEntity.getSubcategories().stream().map(CategoryGetDTO::from).collect(Collectors.toSet())
        );
    }
}
