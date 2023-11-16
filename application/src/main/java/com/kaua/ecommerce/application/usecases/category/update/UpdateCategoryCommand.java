package com.kaua.ecommerce.application.usecases.category.update;

public record UpdateCategoryCommand(
        String id,
        String name,
        String description
) {

    public static UpdateCategoryCommand with(
            final String aId,
            final String aName,
            final String aDescription
    ) {
        return new UpdateCategoryCommand(
                aId,
                aName,
                aDescription
        );
    }
}
