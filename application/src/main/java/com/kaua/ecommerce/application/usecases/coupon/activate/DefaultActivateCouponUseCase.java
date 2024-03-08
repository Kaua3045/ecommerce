package com.kaua.ecommerce.application.usecases.coupon.activate;

import com.kaua.ecommerce.application.gateways.CouponGateway;
import com.kaua.ecommerce.domain.coupon.Coupon;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;

import java.util.Objects;

public class DefaultActivateCouponUseCase extends ActivateCouponUseCase {

    private final CouponGateway couponGateway;

    public DefaultActivateCouponUseCase(final CouponGateway couponGateway) {
        this.couponGateway = Objects.requireNonNull(couponGateway);
    }

    @Override
    public ActivateCouponOutput execute(String aId) {
        final var aCoupon = this.couponGateway.findById(aId)
                .orElseThrow(NotFoundException.with(Coupon.class, aId));

        if (aCoupon.isActive()) {
            return ActivateCouponOutput.from(aCoupon);
        }

        final var aCouponActivated = aCoupon.activate();

        return ActivateCouponOutput.from(this.couponGateway.update(aCouponActivated));
    }
}
