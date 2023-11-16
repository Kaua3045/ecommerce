package com.kaua.ecommerce.application.usecases.category.update.subcategories;

import com.kaua.ecommerce.domain.category.Category;

public record UpdateSubCategoriesOutput(String id) {

    public static UpdateSubCategoriesOutput from(final Category aCategory) {
        return new UpdateSubCategoriesOutput(aCategory.getId().getValue());
    }
}
