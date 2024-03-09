package com.kaua.ecommerce.application.usecases.coupon.validate;

import com.kaua.ecommerce.application.gateways.CouponGateway;
import com.kaua.ecommerce.application.gateways.CouponSlotGateway;
import com.kaua.ecommerce.domain.coupon.Coupon;
import com.kaua.ecommerce.domain.coupon.CouponType;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;

import java.util.Objects;

public class DefaultValidateCouponUseCase extends ValidateCouponUseCase {

    private final CouponGateway couponGateway;
    private final CouponSlotGateway couponSlotGateway;

    public DefaultValidateCouponUseCase(
            final CouponGateway couponGateway,
            final CouponSlotGateway couponSlotGateway
    ) {
        this.couponGateway = Objects.requireNonNull(couponGateway);
        this.couponSlotGateway = Objects.requireNonNull(couponSlotGateway);
    }

    @Override
    public ValidateCouponOutput execute(String aCouponCode) {
        final var aCoupon = this.couponGateway.findByCode(aCouponCode)
                .orElseThrow(NotFoundException.with(Coupon.class, aCouponCode));

        if (aCoupon.getType().equals(CouponType.LIMITED)) {
            if (this.couponSlotGateway.existsByCouponId(aCoupon.getId().getValue())) {
                return ValidateCouponOutput.from(aCoupon, aCoupon.isActiveAndNotExpired());
            }
            return ValidateCouponOutput.from(aCoupon, false);
        }

        return ValidateCouponOutput.from(aCoupon, aCoupon.isActiveAndNotExpired());
    }
}
