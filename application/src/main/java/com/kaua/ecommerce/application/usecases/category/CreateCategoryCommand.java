package com.kaua.ecommerce.application.usecases.category;

public record CreateCategoryCommand(
        String name,
        String description,
        boolean isRoot
) {

    public static CreateCategoryCommand with(
            final String aName,
            final String aDescription,
            final boolean aIsRoot
    ) {
        return new CreateCategoryCommand(
                aName,
                aDescription,
                aIsRoot
        );
    }
}
