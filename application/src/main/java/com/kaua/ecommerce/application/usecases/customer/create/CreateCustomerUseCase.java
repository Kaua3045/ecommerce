package com.kaua.ecommerce.application.usecases.customer.create;

import com.kaua.ecommerce.application.UseCase;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

public abstract class CreateCustomerUseCase extends
        UseCase<Either<NotificationHandler, CreateCustomerOutput>, CreateCustomerCommand> {
}
