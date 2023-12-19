package com.kaua.ecommerce.application.usecases.category.search.retrieve.get;

import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.category.CategoryID;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

public record GetCategoryByIdOutput(
        String id,
        String name,
        String description,
        String slug,
        String parentId,
        int subCategoriesLevel,
        Set<GetCategoryByIdOutput> subCategories,
        Instant createdAt,
        Instant updatedAt
) {

    public static GetCategoryByIdOutput from(final Category aCategory) {
        return new GetCategoryByIdOutput(
                aCategory.getId().getValue(),
                aCategory.getName(),
                aCategory.getDescription(),
                aCategory.getSlug(),
                aCategory.getParentId().map(CategoryID::getValue).orElse(null),
                aCategory.getLevel(),
                aCategory.getSubCategories()
                        .stream()
                        .map(GetCategoryByIdOutput::from).collect(Collectors.toSet()),
                aCategory.getCreatedAt(),
                aCategory.getUpdatedAt()
        );
    }
}
