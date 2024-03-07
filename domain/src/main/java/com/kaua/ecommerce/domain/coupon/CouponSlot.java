package com.kaua.ecommerce.domain.coupon;

import com.kaua.ecommerce.domain.ValueObject;
import com.kaua.ecommerce.domain.utils.IdUtils;

import java.util.Objects;

public class CouponSlot extends ValueObject {

    private final String slotId;
    private final String couponId;

    private CouponSlot(final String aSlotId, final String aCouponId) {
        this.slotId = Objects.requireNonNull(aSlotId, "'slotId' should not be null");
        this.couponId = Objects.requireNonNull(aCouponId, "'couponId' should not be null");
    }

    public static CouponSlot newCouponSlot(final String aCouponId) {
        return new CouponSlot(IdUtils.generate(), aCouponId);
    }

    public static CouponSlot with(final String aSlotId, final String aCouponId) {
        return new CouponSlot(aSlotId, aCouponId);
    }

    public String getSlotId() {
        return slotId;
    }

    public String getCouponId() {
        return couponId;
    }
}
