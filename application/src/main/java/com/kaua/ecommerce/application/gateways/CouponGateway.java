package com.kaua.ecommerce.application.gateways;

import com.kaua.ecommerce.domain.coupon.Coupon;
import com.kaua.ecommerce.domain.pagination.Pagination;
import com.kaua.ecommerce.domain.pagination.SearchQuery;

import java.util.Optional;

public interface CouponGateway {

    Coupon create(Coupon coupon);

    boolean existsByCode(String code);

    Coupon update(Coupon coupon);

    Optional<Coupon> findById(String id);

    Optional<Coupon> findByCode(String code);

    Pagination<Coupon> findAll(SearchQuery aQuery);

    void deleteById(String id);
}
