package com.kaua.ecommerce.application.usecases.coupon.create;

public record CreateCouponCommand(
        String code,
        float percentage,
        float minimumPurchase,
        String expirationDate,
        boolean isActive,
        String type,
        int maxUses
) {

    public static CreateCouponCommand with(
            final String code,
            final float percentage,
            final float minimumPurchase,
            final String expirationDate,
            final boolean isActive,
            final String type,
            final int maxUses
    ) {
        return new CreateCouponCommand(code, percentage, minimumPurchase, expirationDate, isActive, type, maxUses);
    }
}
