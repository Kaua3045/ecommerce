package com.kaua.ecommerce.infrastructure.coupon.slot.persistence;

import com.kaua.ecommerce.domain.coupon.CouponSlot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "coupons_slots")
@Entity
public class CouponSlotJpaEntity {

    @Id
    private String id;

    @Column(name = "coupon_id", nullable = false)
    private String couponId;

    public CouponSlotJpaEntity() {}

    private CouponSlotJpaEntity(final String id, final String couponId) {
        this.id = id;
        this.couponId = couponId;
    }

    public static CouponSlotJpaEntity toEntity(final CouponSlot aCouponSlot) {
        return new CouponSlotJpaEntity(aCouponSlot.getSlotId(), aCouponSlot.getCouponId());
    }

    public CouponSlot toDomain() {
        return CouponSlot.with(getId(), getCouponId());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }
}
