package com.kaua.ecommerce.application.usecases.category.update;

import com.kaua.ecommerce.application.UseCase;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

public abstract class UpdateCategoryUseCase extends
        UseCase<Either<NotificationHandler, UpdateCategoryOutput>, UpdateCategoryCommand> {
}
