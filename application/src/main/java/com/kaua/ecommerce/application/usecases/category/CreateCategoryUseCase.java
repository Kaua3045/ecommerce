package com.kaua.ecommerce.application.usecases.category;

import com.kaua.ecommerce.application.UseCase;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

public abstract class CreateCategoryUseCase extends
        UseCase<Either<NotificationHandler, CreateCategoryOutput>, CreateCategoryCommand> {
}
