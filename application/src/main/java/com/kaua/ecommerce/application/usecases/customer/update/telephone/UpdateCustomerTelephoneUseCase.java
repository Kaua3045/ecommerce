package com.kaua.ecommerce.application.usecases.customer.update.telephone;

import com.kaua.ecommerce.application.UseCase;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

public abstract class UpdateCustomerTelephoneUseCase extends
        UseCase<Either<NotificationHandler, UpdateCustomerTelephoneOutput>, UpdateCustomerTelephoneCommand> {
}
