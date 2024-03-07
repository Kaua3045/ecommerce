package com.kaua.ecommerce.domain.coupon;

import com.kaua.ecommerce.domain.Identifier;
import com.kaua.ecommerce.domain.utils.IdUtils;

import java.util.Objects;

public class CouponID extends Identifier {

    private final String value;

    private CouponID(final String value) {
        this.value = Objects.requireNonNull(value, "'id' should not be null");
    }

    public static CouponID unique() {
        return new CouponID(IdUtils.generate());
    }

    public static CouponID from(final String value) {
        return new CouponID(value);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CouponID couponID = (CouponID) o;
        return Objects.equals(getValue(), couponID.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
