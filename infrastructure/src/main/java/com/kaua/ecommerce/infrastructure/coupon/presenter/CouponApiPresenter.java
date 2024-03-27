package com.kaua.ecommerce.infrastructure.coupon.presenter;

import com.kaua.ecommerce.application.usecases.coupon.retrieve.list.ListCouponsOutput;
import com.kaua.ecommerce.infrastructure.coupon.models.ListCouponsResponse;

public final class CouponApiPresenter {

    private CouponApiPresenter() {
    }

    public static ListCouponsResponse present(final ListCouponsOutput aOutput) {
        return new ListCouponsResponse(
                aOutput.id(),
                aOutput.code(),
                aOutput.percentage(),
                aOutput.minimumPurchaseAmount(),
                aOutput.expirationDate(),
                aOutput.isActive(),
                aOutput.type(),
                aOutput.createdAt()
        );
    }
}
