package com.kaua.ecommerce.application.usecases.product.update;

import com.kaua.ecommerce.application.UseCase;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

public abstract class UpdateProductUseCase extends
        UseCase<Either<NotificationHandler, UpdateProductOutput>, UpdateProductCommand> {
}
