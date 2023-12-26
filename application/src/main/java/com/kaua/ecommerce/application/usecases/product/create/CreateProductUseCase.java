package com.kaua.ecommerce.application.usecases.product.create;

import com.kaua.ecommerce.application.UseCase;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

public abstract class CreateProductUseCase extends
        UseCase<Either<NotificationHandler, CreateProductOutput>, CreateProductCommand> {
}
