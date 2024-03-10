package com.kaua.ecommerce.application.usecases.coupon.update;

public record UpdateCouponCommand(
        String id,
        String code,
        float percentage,
        String expirationDate
) {

    public static UpdateCouponCommand with(
            final String aId,
            final String aCode,
            final float aPercentage,
            final String aExpirationDate
    ) {
        return new UpdateCouponCommand(aId, aCode, aPercentage, aExpirationDate);
    }
}
