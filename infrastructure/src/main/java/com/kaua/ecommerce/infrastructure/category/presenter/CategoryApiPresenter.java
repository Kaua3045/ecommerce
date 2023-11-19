package com.kaua.ecommerce.infrastructure.category.presenter;

import com.kaua.ecommerce.application.usecases.category.retrieve.list.ListCategoriesOutput;
import com.kaua.ecommerce.infrastructure.category.models.ListCategoriesResponse;

import java.util.stream.Collectors;

public final class CategoryApiPresenter {

    private CategoryApiPresenter() {}

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
}