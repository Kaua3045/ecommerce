package com.kaua.ecommerce.application.usecases.category.update;

import java.util.Optional;

public record UpdateCategoryCommand(
        String rootCategoryId,
        Optional<String> subCategoryId,
        String name,
        String description
) {

    public static UpdateCategoryCommand with(
            final String aRootCategoryId,
            final String aSubCategoryId,
            final String aName,
            final String aDescription
    ) {
        return new UpdateCategoryCommand(
                aRootCategoryId,
                Optional.ofNullable(aSubCategoryId),
                aName,
                aDescription
        );
    }
}
