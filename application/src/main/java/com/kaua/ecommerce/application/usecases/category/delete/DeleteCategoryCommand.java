package com.kaua.ecommerce.application.usecases.category.delete;

import java.util.Optional;

public record DeleteCategoryCommand(
        String rootCategoryId,
        Optional<String> subCategoryId
) {

    public static DeleteCategoryCommand with(
            String rootCategoryId,
            String subCategoryId
    ) {
        return new DeleteCategoryCommand(rootCategoryId, Optional.ofNullable(subCategoryId));
    }
}
