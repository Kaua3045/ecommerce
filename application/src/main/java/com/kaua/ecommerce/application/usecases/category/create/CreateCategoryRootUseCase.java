package com.kaua.ecommerce.application.usecases.category.create;

import com.kaua.ecommerce.application.UseCase;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

public abstract class CreateCategoryRootUseCase extends
        UseCase<Either<NotificationHandler, CreateCategoryRootOutput>, CreateCategoryRootCommand> {
}
