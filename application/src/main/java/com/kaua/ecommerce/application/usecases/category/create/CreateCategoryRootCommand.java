package com.kaua.ecommerce.application.usecases.category.create;

public record CreateCategoryRootCommand(
        String name,
        String description
) {

    public static CreateCategoryRootCommand with(
            final String aName,
            final String aDescription
    ) {
        return new CreateCategoryRootCommand(
                aName,
                aDescription
        );
    }
}
