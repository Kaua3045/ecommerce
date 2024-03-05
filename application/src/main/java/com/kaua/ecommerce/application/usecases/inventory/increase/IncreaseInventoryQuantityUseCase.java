package com.kaua.ecommerce.application.usecases.inventory.increase;

import com.kaua.ecommerce.application.UseCase;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

public abstract class IncreaseInventoryQuantityUseCase extends
        UseCase<Either<NotificationHandler, IncreaseInventoryQuantityOutput>, IncreaseInventoryQuantityCommand> {
}
