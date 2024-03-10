package com.kaua.ecommerce.application.usecases.coupon.update;

import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.gateways.CouponGateway;
import com.kaua.ecommerce.domain.coupon.Coupon;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

import java.util.Objects;

public class DefaultUpdateCouponUseCase extends UpdateCouponUseCase {

    private final CouponGateway couponGateway;

    public DefaultUpdateCouponUseCase(final CouponGateway couponGateway) {
        this.couponGateway = Objects.requireNonNull(couponGateway);
    }

    @Override
    public Either<NotificationHandler, UpdateCouponOutput> execute(UpdateCouponCommand input) {
        final var aCoupon = this.couponGateway.findById(input.id())
                .orElseThrow(NotFoundException.with(Coupon.class, input.id()));

        final var aNotification = NotificationHandler.create();

        if (this.couponGateway.existsByCode(input.code())) {
            return Either.left(aNotification.append(new Error("'code' already exists")));
        }

        final var aExpirationDate = input.expirationDate() == null || input.expirationDate().isBlank()
                ? aCoupon.getExpirationDate()
                : InstantUtils.parse(input.expirationDate());

        final var aCouponUpdated = aCoupon.update(input.code(), input.percentage(), aExpirationDate);
        aCouponUpdated.validate(aNotification);

        if (aNotification.hasError()) {
            return Either.left(aNotification);
        }

        return Either.right(UpdateCouponOutput.from(this.couponGateway.update(aCouponUpdated)));
    }
}
