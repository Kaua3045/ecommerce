package com.kaua.ecommerce.application.usecases.inventory.decrease;

import com.kaua.ecommerce.application.UseCase;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

public abstract class DecreaseInventoryQuantityUseCase extends
        UseCase<Either<NotificationHandler, DecreaseInventoryQuantityOutput>, DecreaseInventoryQuantityCommand> {
}
