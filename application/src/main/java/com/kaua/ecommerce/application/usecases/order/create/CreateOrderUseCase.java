package com.kaua.ecommerce.application.usecases.order.create;

import com.kaua.ecommerce.application.UseCase;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

public abstract class CreateOrderUseCase extends
        UseCase<Either<NotificationHandler, CreateOrderOutput>, CreateOrderCommand> {
}
