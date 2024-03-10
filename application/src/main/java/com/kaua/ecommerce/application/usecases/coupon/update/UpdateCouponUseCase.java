package com.kaua.ecommerce.application.usecases.coupon.update;

import com.kaua.ecommerce.application.UseCase;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

public abstract class UpdateCouponUseCase extends
        UseCase<Either<NotificationHandler, UpdateCouponOutput>, UpdateCouponCommand> {
}
