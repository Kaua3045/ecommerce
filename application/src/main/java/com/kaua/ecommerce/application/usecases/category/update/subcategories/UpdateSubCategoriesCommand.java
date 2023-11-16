package com.kaua.ecommerce.application.usecases.category.update.subcategories;

public record UpdateSubCategoriesCommand(
        String id,
        String name,
        String description
) {

    public static UpdateSubCategoriesCommand with(
            final String aId,
            final String aName,
            final String aDescription
    ) {
        return new UpdateSubCategoriesCommand(aId, aName, aDescription);
    }
}
