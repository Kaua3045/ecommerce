package com.kaua.ecommerce.infrastructure.coupon.slot;

import com.kaua.ecommerce.application.gateways.CouponSlotGateway;
import com.kaua.ecommerce.domain.coupon.CouponSlot;
import com.kaua.ecommerce.infrastructure.coupon.slot.persistence.CouponSlotJpaEntity;
import com.kaua.ecommerce.infrastructure.coupon.slot.persistence.CouponSlotJpaEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;

@Component
public class CouponSlotMySQLGateway implements CouponSlotGateway {

    private static final Logger log = LoggerFactory.getLogger(CouponSlotMySQLGateway.class);

    private final CouponSlotJpaEntityRepository couponSlotJpaEntityRepository;

    public CouponSlotMySQLGateway(final CouponSlotJpaEntityRepository couponSlotJpaEntityRepository) {
        this.couponSlotJpaEntityRepository = Objects.requireNonNull(couponSlotJpaEntityRepository);
    }

    @Override
    public Set<CouponSlot> createInBatch(Set<CouponSlot> couponSlots) {
        final var aCouponSlotJpaEntities = couponSlots.stream()
                .map(CouponSlotJpaEntity::toEntity)
                .toList();

        final var aResult = this.couponSlotJpaEntityRepository.saveAll(aCouponSlotJpaEntities);

        log.info("inserted coupon slots: {}", aResult.size());
        return couponSlots;
    }

    @Override
    public boolean existsByCouponId(String couponId) {
        return this.couponSlotJpaEntityRepository.existsByCouponId(couponId);
    }

    @Override
    public void deleteAllByCouponId(String couponId) {
        if (this.couponSlotJpaEntityRepository.existsByCouponId(couponId)) {
            this.couponSlotJpaEntityRepository.deleteAllByCouponId(couponId);
            log.info("deleted coupon slots with coupon id: {}", couponId);
        }
    }

    @Transactional
    @Override
    public boolean deleteFirstSlotByCouponId(String couponId) {
        final var aResult = this.couponSlotJpaEntityRepository.deleteFirstSlotByCouponId(couponId);
        log.info("deleted first coupon slot with coupon id: {}", couponId);
        return aResult == 1;
    }
}
