package com.kaua.ecommerce.application.usecases.coupon.deactivate;

import com.kaua.ecommerce.application.gateways.CouponGateway;
import com.kaua.ecommerce.domain.coupon.Coupon;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;

import java.util.Objects;

public class DefaultDeactivateCouponUseCase extends DeactivateCouponUseCase {

    private final CouponGateway couponGateway;

    public DefaultDeactivateCouponUseCase(final CouponGateway couponGateway) {
        this.couponGateway = Objects.requireNonNull(couponGateway);
    }

    @Override
    public DeactivateCouponOutput execute(String aId) {
        final var aCoupon = this.couponGateway.findById(aId)
                .orElseThrow(NotFoundException.with(Coupon.class, aId));

        if (!aCoupon.isActive()) {
            return DeactivateCouponOutput.from(aCoupon);
        }

        final var aCouponDeactivated = aCoupon.deactivate();

        return DeactivateCouponOutput.from(this.couponGateway.update(aCouponDeactivated));
    }
}
