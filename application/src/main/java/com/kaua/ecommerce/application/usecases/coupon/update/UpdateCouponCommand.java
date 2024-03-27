package com.kaua.ecommerce.application.usecases.coupon.update;

public record UpdateCouponCommand(
        String id,
        String code,
        float percentage,
        float minimumPurchase,
        String expirationDate
) {

    public static UpdateCouponCommand with(
            final String aId,
            final String aCode,
            final float aPercentage,
            final float aMinimumPurchase,
            final String aExpirationDate
    ) {
        return new UpdateCouponCommand(aId, aCode, aPercentage, aMinimumPurchase, aExpirationDate);
    }
}
