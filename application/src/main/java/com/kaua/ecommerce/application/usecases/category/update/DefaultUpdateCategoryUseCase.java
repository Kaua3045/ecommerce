package com.kaua.ecommerce.application.usecases.category.update;

import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.category.events.CategoryUpdatedEvent;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.utils.SlugUtils;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

import java.util.Objects;
import java.util.stream.Collectors;

public class DefaultUpdateCategoryUseCase extends UpdateCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultUpdateCategoryUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Either<NotificationHandler, UpdateCategoryOutput> execute(final UpdateCategoryCommand input) {
        final var aNotification = NotificationHandler.create();

        if (this.categoryGateway.existsByName(input.name())) {
            return Either.left(aNotification.append(new Error("Category already exists")));
        }

        final var aRootCategory = getRootCategory(input.rootCategoryId());

        if (input.subCategoryId().isPresent()) {
            final var aSubCategoryUpdated = aRootCategory.getSubCategories().stream().map(aSubCategory -> {
                if (aSubCategory.getId().getValue().equals(input.subCategoryId().get())) {
                    final var aSubUpdated = updateCategory(aSubCategory, input);
                    aSubUpdated.validate(aNotification);
                    return aSubUpdated;
                } else {
                    final var aSubSubCategoryUpdated = aSubCategory.getSubCategories().stream()
                            .filter(aSubSubCategory -> aSubSubCategory.getId().getValue().equals(input.subCategoryId().get()))
                            .findFirst()
                            .map(aSubSubCategory -> {
                                final var aSubSubUpdated = updateCategory(aSubSubCategory, input);
                                aSubSubUpdated.validate(aNotification);
                                return aSubSubUpdated;
                            });
                    if (aSubSubCategoryUpdated.isPresent()) {
                        aSubCategory.addSubCategory(aSubSubCategoryUpdated.get());
                        return aSubCategory;
                    }
                }
                return aSubCategory;
            }).collect(Collectors.toSet());

            if (aNotification.hasError()) {
                return Either.left(aNotification);
            }

            aRootCategory.addSubCategories(aSubCategoryUpdated);

            return Either.right(updateCategoryAndOutput(aRootCategory));
        }

        final var aName = validateName(input, aRootCategory);
        final var aSlug = nameToSlug(aName);
        final var aCategoryUpdated = aRootCategory.update(aName, input.description(), aSlug);
        aCategoryUpdated.validate(aNotification);

        if (aNotification.hasError()) {
            return Either.left(aNotification);
        }

        aCategoryUpdated.registerEvent(CategoryUpdatedEvent.from(aCategoryUpdated));
        this.categoryGateway.update(aCategoryUpdated);

        return Either.right(UpdateCategoryOutput.from(aCategoryUpdated));
    }

    private Category getRootCategory(final String aRootCategoryId) {
        return this.categoryGateway.findById(aRootCategoryId)
                .orElseThrow(NotFoundException.with(Category.class, aRootCategoryId));
    }

    private Category updateCategory(final Category actualCategory, final UpdateCategoryCommand aInput) {
        final var aName = validateName(aInput, actualCategory);
        final var aSlug = nameToSlug(aName);
        return actualCategory.update(aName, aInput.description(), aSlug);
    }

    public UpdateCategoryOutput updateCategoryAndOutput(final Category aCategory) {
        aCategory.registerEvent(CategoryUpdatedEvent.from(aCategory));
        this.categoryGateway.update(aCategory);
        return UpdateCategoryOutput.from(aCategory);
    }

    private String validateName(final UpdateCategoryCommand input, final Category aOldCategoryName) {
        return input.name() == null || input.name().isBlank()
                ? aOldCategoryName.getName()
                : input.name();
    }

    private String nameToSlug(final String aName) {
        return SlugUtils.createSlug(aName);
    }
}
