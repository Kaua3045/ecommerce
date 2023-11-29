package com.kaua.ecommerce.application.usecases.category.create;

import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.category.events.CategoryCreatedEvent;
import com.kaua.ecommerce.domain.utils.SlugUtils;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

import java.util.Objects;

public class DefaultCreateCategoryRootUseCase extends CreateCategoryRootUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultCreateCategoryRootUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Either<NotificationHandler, CreateCategoryRootOutput> execute(CreateCategoryRootCommand input) {
        final var aNotification = NotificationHandler.create();

        if (this.categoryGateway.existsByName(input.name())) {
            return Either.left(aNotification.append(new Error("Category already exists")));
        }

        final var aSlug = nameToSlug(input.name());

        final var aCategory = Category.newCategory(
                input.name(),
                input.description(),
                aSlug,
                null
        );
        aCategory.validate(aNotification);

        if (aNotification.hasError()) {
            return Either.left(aNotification);
        }

        aCategory.registerEvent(CategoryCreatedEvent.from(aCategory));
        this.categoryGateway.create(aCategory);

        return Either.right(CreateCategoryRootOutput.from(aCategory));
    }

    private String nameToSlug(final String aName) {
        return SlugUtils.createSlug(aName);
    }
}
