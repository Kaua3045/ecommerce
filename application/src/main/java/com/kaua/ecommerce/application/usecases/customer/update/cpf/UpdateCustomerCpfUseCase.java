package com.kaua.ecommerce.application.usecases.customer.update.cpf;

import com.kaua.ecommerce.application.UseCase;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

public abstract class UpdateCustomerCpfUseCase
        extends UseCase<Either<NotificationHandler, UpdateCustomerCpfOutput>, UpdateCustomerCpfCommand> {
}
