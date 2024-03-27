package com.kaua.ecommerce.domain.coupon;

import com.kaua.ecommerce.domain.AggregateRoot;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.validation.ValidationHandler;

import java.time.Instant;

public class Coupon extends AggregateRoot<CouponID> {

    private CouponCode code;
    private float percentage;
    private float minimumPurchaseAmount;
    private Instant expirationDate;
    private boolean isActive;
    private CouponType type;
    private Instant createdAt;
    private Instant updatedAt;

    private Coupon(
            final CouponID aCouponID,
            final CouponCode aCode,
            final float aPercentage,
            final float aMinimumPurchaseAmount,
            final Instant aExpirationDate,
            final boolean aIsActive,
            final CouponType aType,
            final Instant aCreatedAt,
            final Instant aUpdatedAt,
            final long aVersion
    ) {
        super(aCouponID, aVersion);
        this.code = aCode;
        this.percentage = aPercentage;
        this.minimumPurchaseAmount = aMinimumPurchaseAmount;
        this.expirationDate = aExpirationDate;
        this.isActive = aIsActive;
        this.type = aType;
        this.createdAt = aCreatedAt;
        this.updatedAt = aUpdatedAt;
    }

    public static Coupon newCoupon(
            final String aCode,
            final float aPercentage,
            final float aMinimumPurchaseAmount,
            final Instant aExpirationDate,
            final boolean aIsActive,
            final CouponType aType
    ) {
        final var aId = CouponID.unique();
        final var aNow = InstantUtils.now();
        final var aCouponCode = CouponCode.create(aCode);
        return new Coupon(aId, aCouponCode, aPercentage, aMinimumPurchaseAmount, aExpirationDate, aIsActive, aType, aNow, aNow, 0);
    }

    public static Coupon with(
            final String aCouponID,
            final String aCode,
            final float aPercentage,
            final float aMinimumPurchaseAmount,
            final Instant aExpirationDate,
            final boolean aIsActive,
            final CouponType aType,
            final Instant aCreatedAt,
            final Instant aUpdatedAt,
            final long aVersion
    ) {
        return new Coupon(
                CouponID.from(aCouponID),
                CouponCode.create(aCode),
                aPercentage,
                aMinimumPurchaseAmount,
                aExpirationDate,
                aIsActive,
                aType,
                aCreatedAt,
                aUpdatedAt,
                aVersion
        );
    }

    public static Coupon with(final Coupon aCoupon) {
        return new Coupon(
                aCoupon.getId(),
                aCoupon.getCode(),
                aCoupon.getPercentage(),
                aCoupon.getMinimumPurchaseAmount(),
                aCoupon.getExpirationDate(),
                aCoupon.isActive(),
                aCoupon.getType(),
                aCoupon.getCreatedAt(),
                aCoupon.getUpdatedAt(),
                aCoupon.getVersion()
        );
    }

    public boolean isExpired() {
        return this.expirationDate.isBefore(InstantUtils.now());
    }

    public boolean isActiveAndNotExpired() {
        return this.isActive && !this.isExpired();
    }

    public Coupon activate() {
        this.isActive = true;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Coupon deactivate() {
        this.isActive = false;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Coupon update(final String aCode, final float aPercentage, final float aMinimumPurchaseAmount, final Instant aExpirationDate) {
        this.code = CouponCode.create(aCode);
        this.percentage = aPercentage;
        this.minimumPurchaseAmount = aMinimumPurchaseAmount;
        this.expirationDate = aExpirationDate;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    @Override
    public void validate(ValidationHandler handler) {
        new CouponValidation(handler, this).validate();
    }

    public CouponCode getCode() {
        return code;
    }

    public float getPercentage() {
        return percentage;
    }

    public float getMinimumPurchaseAmount() {
        return minimumPurchaseAmount;
    }

    public Instant getExpirationDate() {
        return expirationDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public CouponType getType() {
        return type;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Coupon(" +
                "id='" + getId().getValue() + '\'' +
                ", code='" + code.getValue() + '\'' +
                ", percentage=" + percentage +
                ", expirationDate=" + expirationDate +
                ", isActive=" + isActive +
                ", type=" + type.name() +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", version=" + getVersion() +
                ')';
    }
}
