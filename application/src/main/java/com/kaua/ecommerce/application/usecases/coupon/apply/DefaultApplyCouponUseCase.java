package com.kaua.ecommerce.application.usecases.coupon.apply;

import com.kaua.ecommerce.application.exceptions.CouponMinimumPurchaseAmountException;
import com.kaua.ecommerce.application.exceptions.CouponNoMoreAvailableException;
import com.kaua.ecommerce.application.gateways.CouponGateway;
import com.kaua.ecommerce.application.gateways.CouponSlotGateway;
import com.kaua.ecommerce.domain.coupon.Coupon;
import com.kaua.ecommerce.domain.coupon.CouponType;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;

import java.util.Objects;

public class DefaultApplyCouponUseCase extends ApplyCouponUseCase {

    private final CouponGateway couponGateway;
    private final CouponSlotGateway couponSlotGateway;

    public DefaultApplyCouponUseCase(
            final CouponGateway couponGateway,
            final CouponSlotGateway couponSlotGateway
    ) {
        this.couponGateway = Objects.requireNonNull(couponGateway);
        this.couponSlotGateway = Objects.requireNonNull(couponSlotGateway);
    }

    @Override
    public ApplyCouponOutput execute(final ApplyCouponCommand aCommand) {
        final var aCoupon = this.couponGateway.findByCode(aCommand.couponCode())
                .orElseThrow(NotFoundException.with(Coupon.class, aCommand.couponCode()));

        if (aCommand.totalAmount() < aCoupon.getMinimumPurchaseAmount()) throw new CouponMinimumPurchaseAmountException();

        if (aCoupon.getType().equals(CouponType.LIMITED)) {
            if (!aCoupon.isActiveAndNotExpired()) throw new CouponNoMoreAvailableException();

            final var aCouponSlotDeleteResult = this.couponSlotGateway.deleteFirstSlotByCouponId(
                    aCoupon.getId().getValue()
            );

            if (!aCouponSlotDeleteResult) {
                throw new CouponNoMoreAvailableException();
            }
        }

        return ApplyCouponOutput.from(aCoupon);
    }
}
