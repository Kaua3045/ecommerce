package com.kaua.ecommerce.infrastructure.coupon.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponJpaEntityRepository extends JpaRepository<CouponJpaEntity, String> {

    boolean existsByCode(String code);
}
