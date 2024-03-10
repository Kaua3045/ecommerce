package com.kaua.ecommerce.application.coupon.retrieve.list;

import com.kaua.ecommerce.application.usecases.coupon.retrieve.list.ListCouponsUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.pagination.SearchQuery;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntity;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@IntegrationTest
public class ListCouponsUseCaseIT {

    @Autowired
    private CouponJpaEntityRepository couponJpaEntityRepository;

    @Autowired
    private ListCouponsUseCase listCouponsUseCase;

    @Test
    void givenAPrePersistedCouponsAndQueryWithTypeLimitedTerm_whenListCoupons_thenShouldReturnAPageOfCoupons() {
        final var aCouponLimited = Fixture.Coupons.limitedCouponActivated();
        final var aCouponUnlimited = Fixture.Coupons.unlimitedCouponActivated();

        this.couponJpaEntityRepository.saveAll(Set.of(
                CouponJpaEntity.toEntity(aCouponLimited),
                CouponJpaEntity.toEntity(aCouponUnlimited)
        ));

        Assertions.assertEquals(2, this.couponJpaEntityRepository.count());

        final var aQuery = new SearchQuery(0, 10, "LIMITED", "code", "ASC");

        final var aResult = this.listCouponsUseCase.execute(aQuery);

        Assertions.assertEquals(0, aResult.currentPage());
        Assertions.assertEquals(10, aResult.perPage());
        Assertions.assertEquals(1, aResult.totalPages());
        Assertions.assertEquals(1, aResult.totalItems());
        Assertions.assertEquals(1, aResult.items().size());

        final var aCoupon = aResult.items().get(0);
        Assertions.assertEquals(aCouponLimited.getId().getValue(), aCoupon.id());
        Assertions.assertEquals(aCouponLimited.getCode().getValue(), aCoupon.code());
        Assertions.assertEquals(aCouponLimited.getType().name(), aCoupon.type());
        Assertions.assertEquals(aCouponLimited.getPercentage(), aCoupon.percentage());
        Assertions.assertEquals(aCouponLimited.getExpirationDate(), aCoupon.expirationDate());
    }

    @Test
    void givenAPrePersistedCouponsAndQueryWithTypeUnlimited_whenListCoupons_thenShouldReturnAPageOfCoupons() {
        final var aCouponLimited = Fixture.Coupons.limitedCouponActivated();
        final var aCouponUnlimited = Fixture.Coupons.unlimitedCouponActivated();

        this.couponJpaEntityRepository.saveAll(Set.of(
                CouponJpaEntity.toEntity(aCouponLimited),
                CouponJpaEntity.toEntity(aCouponUnlimited)
        ));

        Assertions.assertEquals(2, this.couponJpaEntityRepository.count());

        final var aQuery = new SearchQuery(0, 10, "UNLIMITED", "code", "ASC");

        final var aResult = this.listCouponsUseCase.execute(aQuery);

        Assertions.assertEquals(0, aResult.currentPage());
        Assertions.assertEquals(10, aResult.perPage());
        Assertions.assertEquals(1, aResult.totalPages());
        Assertions.assertEquals(1, aResult.totalItems());
        Assertions.assertEquals(1, aResult.items().size());

        final var aCoupon = aResult.items().get(0);
        Assertions.assertEquals(aCouponUnlimited.getId().getValue(), aCoupon.id());
        Assertions.assertEquals(aCouponUnlimited.getCode().getValue(), aCoupon.code());
        Assertions.assertEquals(aCouponUnlimited.getType().name(), aCoupon.type());
        Assertions.assertEquals(aCouponUnlimited.getPercentage(), aCoupon.percentage());
        Assertions.assertEquals(aCouponUnlimited.getExpirationDate(), aCoupon.expirationDate());
    }
}
