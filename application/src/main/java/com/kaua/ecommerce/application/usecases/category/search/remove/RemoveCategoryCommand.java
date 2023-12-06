package com.kaua.ecommerce.application.usecases.category.search.remove;

import java.util.Optional;

public record RemoveCategoryCommand(
        String rootCategoryId,
        Optional<String> subCategoryId
) {

    public static RemoveCategoryCommand with(
            String rootCategoryId,
            String subCategoryId
    ) {
        return new RemoveCategoryCommand(rootCategoryId, Optional.ofNullable(subCategoryId));
    }
}
