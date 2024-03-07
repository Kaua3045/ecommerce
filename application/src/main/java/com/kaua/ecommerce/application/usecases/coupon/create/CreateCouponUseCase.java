package com.kaua.ecommerce.application.usecases.coupon.create;

import com.kaua.ecommerce.application.UseCase;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

public abstract class CreateCouponUseCase extends
        UseCase<Either<NotificationHandler, CreateCouponOutput>, CreateCouponCommand> {
}
