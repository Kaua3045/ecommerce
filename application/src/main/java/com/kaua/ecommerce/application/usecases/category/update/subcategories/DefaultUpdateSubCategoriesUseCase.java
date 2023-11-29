package com.kaua.ecommerce.application.usecases.category.update.subcategories;

import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.category.events.CategoryUpdatedEvent;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.utils.SlugUtils;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultUpdateSubCategoriesUseCase extends UpdateSubCategoriesUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultUpdateSubCategoriesUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Either<NotificationHandler, UpdateSubCategoriesOutput> execute(UpdateSubCategoriesCommand input) {
        final var aNotification = NotificationHandler.create();

        if (this.categoryGateway.existsByName(input.name())) {
            return Either.left(aNotification.append(new Error("Category already exists")));
        }

        final var aCategoryRoot = getRootCategory(input.rootCategoryId());

        final var aSlug = nameToSlug(input.name());

        Category aSubCategory;

        if (input.subCategoryId().isPresent()) {
            aSubCategory = createSubCategory(input, aSlug, aCategoryRoot);
            aSubCategory.validate(aNotification);

            if (aNotification.hasError()) {
                return Either.left(aNotification);
            }

            updateSubCategory(input.subCategoryId().get(), aCategoryRoot, aSubCategory);

            return Either.right(updateCategoryAndOutput(aCategoryRoot));
        }

        aSubCategory = createSubCategory(input, aSlug, aCategoryRoot);
        aSubCategory.validate(aNotification);

        if (aNotification.hasError()) {
            return Either.left(aNotification);
        }

        addSubCategoryAndUpdate(aCategoryRoot, aSubCategory);

        return Either.right(updateCategoryAndOutput(aCategoryRoot));
    }

    private Category getRootCategory(final String aRootCategoryId) {
        return this.categoryGateway.findById(aRootCategoryId)
                .orElseThrow(NotFoundException.with(Category.class, aRootCategoryId));
    }

    private Category createSubCategory(
            final UpdateSubCategoriesCommand aInput,
            final String aSlug,
            final Category aCategoryRoot
    ) {
        if (aInput.subCategoryId().isPresent()) {
            return Category.newCategory(
                    aInput.name(),
                    aInput.description(),
                    aSlug,
                    CategoryID.from(aInput.subCategoryId().get())
            );
        } else {
            return Category.newCategory(
                    aInput.name(),
                    aInput.description(),
                    aSlug,
                    aCategoryRoot.getId()
            );
        }
    }

    private void updateSubCategory(
            final String aSubCategoryId,
            final Category aCategoryRoot,
            final Category aSubCategory) {
        Set<Category> updatedSubCategories = aCategoryRoot.getSubCategories().stream()
                .map(subCategories -> {
                    if (subCategories.getId().getValue().equals(aSubCategoryId)) {
                        subCategories.addSubCategory(aSubCategory);
                        subCategories.updateSubCategoriesLevel();
                        return subCategories;
                    }
                    throw NotFoundException.with(Category.class, aSubCategoryId).get();
                }).collect(Collectors.toSet());

        aCategoryRoot.addSubCategories(updatedSubCategories);
        aCategoryRoot.registerEvent(CategoryUpdatedEvent.from(aCategoryRoot));
    }

    private void addSubCategoryAndUpdate(final Category aCategoryRoot, final Category aSubCategory) {
        aCategoryRoot.addSubCategory(aSubCategory);
        aCategoryRoot.updateSubCategoriesLevel();
        aCategoryRoot.registerEvent(CategoryUpdatedEvent.from(aCategoryRoot));
    }

    private UpdateSubCategoriesOutput updateCategoryAndOutput(final Category aCategoryRoot) {
        return UpdateSubCategoriesOutput.from(this.categoryGateway.update(aCategoryRoot));
    }

    private String nameToSlug(final String aName) {
        return SlugUtils.createSlug(aName);
    }
}
