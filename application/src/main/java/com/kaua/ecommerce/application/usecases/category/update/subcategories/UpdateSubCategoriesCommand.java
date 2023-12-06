package com.kaua.ecommerce.application.usecases.category.update.subcategories;

import java.util.Optional;

public record UpdateSubCategoriesCommand(
        String rootCategoryId,
        Optional<String> subCategoryId,
        String name,
        String description
) {

    public static UpdateSubCategoriesCommand with(
            final String aRootCategoryId,
            final String aSubCategoryId,
            final String aName,
            final String aDescription
    ) {
        return new UpdateSubCategoriesCommand(aRootCategoryId, Optional.ofNullable(aSubCategoryId), aName, aDescription);
    }
}
