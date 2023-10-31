package com.kaua.ecommerce.application.usecases.customer.update.address;

import com.kaua.ecommerce.application.UseCase;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

public abstract class UpdateCustomerAddressUseCase extends
        UseCase<Either<NotificationHandler, UpdateCustomerAddressOutput>, UpdateCustomerAddressCommand> {
}
