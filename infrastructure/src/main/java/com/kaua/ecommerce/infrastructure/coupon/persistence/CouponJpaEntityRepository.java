package com.kaua.ecommerce.infrastructure.coupon.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponJpaEntityRepository extends JpaRepository<CouponJpaEntity, String> {

    boolean existsByCode(String code);

    Optional<CouponJpaEntity> findByCode(String code);
}
