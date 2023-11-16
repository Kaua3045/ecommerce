package com.kaua.ecommerce.application.usecases.category.update.subcategories;

import com.kaua.ecommerce.application.UseCase;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

public abstract class UpdateSubCategoriesUseCase extends
        UseCase<Either<NotificationHandler, UpdateSubCategoriesOutput>, UpdateSubCategoriesCommand> {
}
