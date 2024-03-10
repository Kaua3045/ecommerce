package com.kaua.ecommerce.application.usecases.coupon.retrieve.list;

import com.kaua.ecommerce.application.gateways.CouponGateway;
import com.kaua.ecommerce.domain.pagination.Pagination;
import com.kaua.ecommerce.domain.pagination.SearchQuery;

import java.util.Objects;

public class DefaultListCouponsUseCase extends ListCouponsUseCase {

    private final CouponGateway couponGateway;

    public DefaultListCouponsUseCase(final CouponGateway couponGateway) {
        this.couponGateway = Objects.requireNonNull(couponGateway);
    }

    @Override
    public Pagination<ListCouponsOutput> execute(SearchQuery aQuery) {
        return this.couponGateway.findAll(aQuery)
                .map(ListCouponsOutput::from);
    }
}
