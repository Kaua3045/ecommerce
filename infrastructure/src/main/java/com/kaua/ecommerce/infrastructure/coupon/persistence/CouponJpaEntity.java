package com.kaua.ecommerce.infrastructure.coupon.persistence;

import com.kaua.ecommerce.domain.coupon.Coupon;
import com.kaua.ecommerce.domain.coupon.CouponType;
import jakarta.persistence.*;

import java.time.Instant;

@Table(name = "coupons")
@Entity
public class CouponJpaEntity {

    @Id
    private String id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "percentage", nullable = false)
    private float percentage;

    @Column(name = "expiration_date", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant expirationDate;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CouponType type;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant updatedAt;

    @Version
    private long version;

    public CouponJpaEntity() {
    }

    private CouponJpaEntity(
            final String id,
            final String code,
            final float percentage,
            final Instant expirationDate,
            final boolean isActive,
            final CouponType type,
            final Instant createdAt,
            final Instant updatedAt,
            final long version
    ) {
        this.id = id;
        this.code = code;
        this.percentage = percentage;
        this.expirationDate = expirationDate;
        this.isActive = isActive;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static CouponJpaEntity toEntity(final Coupon aCoupon) {
        return new CouponJpaEntity(
                aCoupon.getId().getValue(),
                aCoupon.getCode().getValue(),
                aCoupon.getPercentage(),
                aCoupon.getExpirationDate(),
                aCoupon.isActive(),
                aCoupon.getType(),
                aCoupon.getCreatedAt(),
                aCoupon.getUpdatedAt(),
                aCoupon.getVersion()
        );
    }

    public Coupon toDomain() {
        return Coupon.with(
                getId(),
                getCode(),
                getPercentage(),
                getExpirationDate(),
                isActive(),
                getType(),
                getCreatedAt(),
                getUpdatedAt(),
                getVersion()
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public Instant getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Instant expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public CouponType getType() {
        return type;
    }

    public void setType(CouponType type) {
        this.type = type;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getVersion() {
        return version;
    }
}
