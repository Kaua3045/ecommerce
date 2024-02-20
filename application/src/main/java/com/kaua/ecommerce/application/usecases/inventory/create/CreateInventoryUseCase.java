package com.kaua.ecommerce.application.usecases.inventory.create;

import com.kaua.ecommerce.application.UseCase;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

public abstract class CreateInventoryUseCase extends
        UseCase<Either<NotificationHandler, CreateInventoryOutput>, CreateInventoryCommand> {
}
