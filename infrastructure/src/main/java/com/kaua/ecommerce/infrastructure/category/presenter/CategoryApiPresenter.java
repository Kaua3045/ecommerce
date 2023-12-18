package com.kaua.ecommerce.infrastructure.category.presenter;

import com.kaua.ecommerce.application.usecases.category.search.retrieve.get.GetCategoryByIdOutput;
import com.kaua.ecommerce.application.usecases.category.search.retrieve.list.ListCategoriesOutput;
import com.kaua.ecommerce.infrastructure.category.models.GetCategoryResponse;
import com.kaua.ecommerce.infrastructure.category.models.ListCategoriesResponse;

import java.util.stream.Collectors;

public final class CategoryApiPresenter {

    private CategoryApiPresenter() {
    }

    public static ListCategoriesResponse present(final ListCategoriesOutput aOutput) {
        return new ListCategoriesResponse(
                aOutput.id(),
                aOutput.name(),
                aOutput.description(),
                aOutput.slug(),
                aOutput.parentId(),
                aOutput.subCategories().stream()
                        .map(CategoryApiPresenter::present).collect(Collectors.toSet()),
                aOutput.createdAt(),
                aOutput.updatedAt()
        );
    }

    public static GetCategoryResponse present(final GetCategoryByIdOutput aOutput) {
        return new GetCategoryResponse(
                aOutput.id(),
                aOutput.name(),
                aOutput.description(),
                aOutput.slug(),
                aOutput.parentId(),
                aOutput.subCategoriesLevel(),
                aOutput.subCategories().stream()
                        .map(CategoryApiPresenter::present).collect(Collectors.toSet()),
                aOutput.createdAt(),
                aOutput.updatedAt()
        );
    }
}
