package com.kaua.ecommerce.infrastructure.coupon;

import com.kaua.ecommerce.application.gateways.CouponGateway;
import com.kaua.ecommerce.domain.coupon.Coupon;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntity;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class CouponMySQLGateway implements CouponGateway {

    private static final Logger log = LoggerFactory.getLogger(CouponMySQLGateway.class);

    private final CouponJpaEntityRepository couponJpaEntityRepository;

    public CouponMySQLGateway(final CouponJpaEntityRepository couponJpaEntityRepository) {
        this.couponJpaEntityRepository = Objects.requireNonNull(couponJpaEntityRepository);
    }

    @Override
    public Coupon create(final Coupon coupon) {
        final var aResult = this.couponJpaEntityRepository.save(CouponJpaEntity.toEntity(coupon))
                .toDomain();

        log.info("inserted coupon: {}", aResult);
        return aResult;
    }

    @Override
    public boolean existsByCode(final String code) {
        return this.couponJpaEntityRepository.existsByCode(code);
    }

    @Override
    public Coupon update(Coupon coupon) {
        final var aResult = this.couponJpaEntityRepository.save(CouponJpaEntity.toEntity(coupon))
                .toDomain();

        log.info("updated coupon: {}", aResult);
        return aResult;
    }

    @Override
    public Optional<Coupon> findById(String id) {
        return this.couponJpaEntityRepository.findById(id)
                .map(CouponJpaEntity::toDomain);
    }
}
