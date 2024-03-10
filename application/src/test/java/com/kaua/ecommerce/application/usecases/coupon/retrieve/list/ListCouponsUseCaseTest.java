package com.kaua.ecommerce.application.usecases.coupon.retrieve.list;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.CouponGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.coupon.Coupon;
import com.kaua.ecommerce.domain.pagination.Pagination;
import com.kaua.ecommerce.domain.pagination.SearchQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

public class ListCouponsUseCaseTest extends UseCaseTest {

    @Mock
    private CouponGateway couponGateway;

    @InjectMocks
    private DefaultListCouponsUseCase listCouponsUseCase;

    @Test
    void givenAValidQuery_whenCallListCouponsUseCase_shouldReturnACoupons() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();
        final var aCoupons = List.of(
                aCoupon,
                Fixture.Coupons.unlimitedCouponActivated());

        final var aPage = 0;
        final var aPerPage = 10;
        final var aTotalPages = 1;
        final var aTerms = "";
        final var aSort = "code";
        final var aDirection = "asc";

        final var aQuery = new SearchQuery(aPage, aPerPage, aTerms, aSort, aDirection);
        final var aPagination = new Pagination<>(aPage, aPerPage, aTotalPages, aCoupons.size(), aCoupons);

        final var aItemsCount = 2;
        final var aResult = aPagination.map(ListCouponsOutput::from);

        Mockito.when(couponGateway.findAll(aQuery)).thenReturn(aPagination);

        final var actualResult = this.listCouponsUseCase.execute(aQuery);

        Assertions.assertEquals(aItemsCount, actualResult.totalItems());
        Assertions.assertEquals(aResult, actualResult);
        Assertions.assertEquals(aPage, actualResult.currentPage());
        Assertions.assertEquals(aPerPage, actualResult.perPage());
        Assertions.assertEquals(aTotalPages, actualResult.totalPages());
        Assertions.assertEquals(aCoupons.size(), actualResult.items().size());
    }

    @Test
    void givenAValidQueryButHasNoData_whenCallListCouponsUseCase_shouldReturnEmptyCoupons() {
        final var aCoupons = List.<Coupon>of();

        final var aPage = 0;
        final var aPerPage = 10;
        final var aTotalPages = 1;
        final var aTerms = "";
        final var aSort = "code";
        final var aDirection = "asc";

        final var aQuery = new SearchQuery(aPage, aPerPage, aTerms, aSort, aDirection);
        final var aPagination = new Pagination<>(aPage, aPerPage, aTotalPages, aCoupons.size(), aCoupons);

        final var aItemsCount = 0;
        final var aResult = aPagination.map(ListCouponsOutput::from);

        Mockito.when(couponGateway.findAll(aQuery)).thenReturn(aPagination);

        final var actualResult = this.listCouponsUseCase.execute(aQuery);

        Assertions.assertEquals(aItemsCount, actualResult.totalItems());
        Assertions.assertEquals(aResult, actualResult);
        Assertions.assertEquals(aPage, actualResult.currentPage());
        Assertions.assertEquals(aPerPage, actualResult.perPage());
        Assertions.assertEquals(aTotalPages, actualResult.totalPages());
        Assertions.assertEquals(aCoupons.size(), actualResult.items().size());
    }
}
