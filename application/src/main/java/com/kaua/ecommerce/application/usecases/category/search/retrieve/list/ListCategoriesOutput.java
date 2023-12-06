package com.kaua.ecommerce.application.usecases.category.search.retrieve.list;

import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.category.CategoryID;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

public record ListCategoriesOutput(
        String id,
        String name,
        String description,
        String slug,
        String parentId,
        Set<ListCategoriesOutput> subCategories,
        Instant createdAt,
        Instant updatedAt
) {

    public static ListCategoriesOutput from(final Category aCategory) {
        return new ListCategoriesOutput(
                aCategory.getId().getValue(),
                aCategory.getName(),
                aCategory.getDescription(),
                aCategory.getSlug(),
                aCategory.getParentId().map(CategoryID::getValue).orElse(null),
                aCategory.getSubCategories().stream()
                        .map(ListCategoriesOutput::from).collect(Collectors.toSet()),
                aCategory.getCreatedAt(),
                aCategory.getUpdatedAt()
        );
    }
}
