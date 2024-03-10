package com.kaua.ecommerce.infrastructure.coupon;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.coupon.Coupon;
import com.kaua.ecommerce.domain.coupon.CouponType;
import com.kaua.ecommerce.domain.pagination.SearchQuery;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.infrastructure.DatabaseGatewayTest;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntity;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.temporal.ChronoUnit;
import java.util.List;

@DatabaseGatewayTest
public class CouponGatewayTest {

    @Autowired
    private CouponMySQLGateway couponGateway;

    @Autowired
    private CouponJpaEntityRepository couponJpaRepository;

    @Test
    void givenAValidCoupon_whenCallCreate_thenShouldCreateCoupon() {
        final var aCode = "BLACK_FRIDAY";
        final var aPercentage = 10.5f;
        final var aExpirationDate = InstantUtils.now();
        final var aIsActive = true;
        final var aType = CouponType.UNLIMITED;

        final var aCoupon = Coupon.newCoupon(
                aCode,
                aPercentage,
                aExpirationDate,
                aIsActive,
                aType
        );

        Assertions.assertEquals(0, this.couponJpaRepository.count());

        final var aOutput = this.couponGateway.create(aCoupon);

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aCoupon.getId().getValue(), aOutput.getId().getValue());
        Assertions.assertEquals(aCode, aOutput.getCode().getValue());
        Assertions.assertEquals(aPercentage, aOutput.getPercentage());
        Assertions.assertEquals(aExpirationDate, aOutput.getExpirationDate());
        Assertions.assertEquals(aIsActive, aOutput.isActive());
        Assertions.assertEquals(aType, aOutput.getType());
        Assertions.assertNotNull(aOutput.getCreatedAt());
        Assertions.assertNotNull(aOutput.getUpdatedAt());
        Assertions.assertEquals(1, this.couponJpaRepository.count());
    }

    @Test
    void givenAValidCode_whenCallExistsByCode_thenShouldReturnTrue() {
        final var aCode = "BLACK_FRIDAY";
        final var aPercentage = 10.5f;
        final var aExpirationDate = InstantUtils.now();
        final var aIsActive = true;
        final var aType = CouponType.UNLIMITED;

        final var aCoupon = Coupon.newCoupon(
                aCode,
                aPercentage,
                aExpirationDate,
                aIsActive,
                aType
        );

        this.couponGateway.create(aCoupon);

        final var aOutput = this.couponGateway.existsByCode(aCode);

        Assertions.assertTrue(aOutput);
    }

    @Test
    void givenAnInvalidCode_whenCallExistsByCode_thenShouldReturnFalse() {
        final var aCode = "BLACK_FRIDAY";
        final var aOutput = this.couponGateway.existsByCode(aCode);
        Assertions.assertFalse(aOutput);
    }

    @Test
    void givenAValidId_whenCallFindById_thenShouldReturnCoupon() {
        final var aCode = "BLACK_FRIDAY";
        final var aPercentage = 10.5f;
        final var aExpirationDate = InstantUtils.now();
        final var aIsActive = true;
        final var aType = CouponType.UNLIMITED;

        final var aCoupon = Coupon.newCoupon(
                aCode,
                aPercentage,
                aExpirationDate,
                aIsActive,
                aType
        );

        this.couponGateway.create(aCoupon);

        final var aOutput = this.couponGateway.findById(aCoupon.getId().getValue());

        Assertions.assertTrue(aOutput.isPresent());
        Assertions.assertEquals(aCoupon.getId().getValue(), aOutput.get().getId().getValue());
        Assertions.assertEquals(aCode, aOutput.get().getCode().getValue());
        Assertions.assertEquals(aPercentage, aOutput.get().getPercentage());
        Assertions.assertEquals(aExpirationDate, aOutput.get().getExpirationDate());
        Assertions.assertEquals(aIsActive, aOutput.get().isActive());
        Assertions.assertEquals(aType, aOutput.get().getType());
        Assertions.assertNotNull(aOutput.get().getCreatedAt());
        Assertions.assertNotNull(aOutput.get().getUpdatedAt());
    }

    @Test
    void givenAnInvalidId_whenCallFindById_thenShouldReturnEmpty() {
        final var aOutput = this.couponGateway.findById("invalid-id");
        Assertions.assertTrue(aOutput.isEmpty());
    }

    @Test
    void givenAValidCoupon_whenCallUpdate_thenShouldUpdateCoupon() {
        final var aCode = "BLACK_FRIDAY";
        final var aPercentage = 10.5f;
        final var aExpirationDate = InstantUtils.now();
        final var aIsActive = true;
        final var aType = CouponType.UNLIMITED;

        final var aCoupon = Coupon.newCoupon(
                aCode,
                aPercentage,
                aExpirationDate,
                aIsActive,
                aType
        );

        this.couponGateway.create(aCoupon);

        final var aCouponUpdated = aCoupon.deactivate();

        final var aOutput = this.couponGateway.update(aCouponUpdated);

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aCoupon.getId().getValue(), aOutput.getId().getValue());
        Assertions.assertEquals(aCode, aOutput.getCode().getValue());
        Assertions.assertEquals(aPercentage, aOutput.getPercentage());
        Assertions.assertEquals(aExpirationDate, aOutput.getExpirationDate());
        Assertions.assertFalse(aOutput.isActive());
        Assertions.assertEquals(aType, aOutput.getType());
        Assertions.assertNotNull(aOutput.getCreatedAt());
        Assertions.assertNotNull(aOutput.getUpdatedAt());
    }

    @Test
    void givenAValidId_whenCallDelete_thenShouldDeleteCoupon() {
        final var aCode = "BLACK_FRIDAY";
        final var aPercentage = 10.5f;
        final var aExpirationDate = InstantUtils.now();
        final var aIsActive = true;
        final var aType = CouponType.UNLIMITED;

        final var aCoupon = Coupon.newCoupon(
                aCode,
                aPercentage,
                aExpirationDate,
                aIsActive,
                aType
        );

        this.couponGateway.create(aCoupon);

        Assertions.assertEquals(1, this.couponJpaRepository.count());

        this.couponGateway.deleteById(aCoupon.getId().getValue());

        Assertions.assertEquals(0, this.couponJpaRepository.count());
    }

    @Test
    void givenAnInvalidId_whenCallDelete_thenShouldDoNothing() {
        Assertions.assertEquals(0, this.couponJpaRepository.count());
        this.couponGateway.deleteById("invalid-id");
        Assertions.assertEquals(0, this.couponJpaRepository.count());
    }

    @Test
    void givenAValidCode_whenCallFindByCode_thenShouldReturnCoupon() {
        final var aCode = "BLACK_FRIDAY";
        final var aPercentage = 10.5f;
        final var aExpirationDate = InstantUtils.now();
        final var aIsActive = true;
        final var aType = CouponType.UNLIMITED;

        final var aCoupon = Coupon.newCoupon(
                aCode,
                aPercentage,
                aExpirationDate,
                aIsActive,
                aType
        );

        this.couponGateway.create(aCoupon);

        final var aOutput = this.couponGateway.findByCode(aCode);

        Assertions.assertTrue(aOutput.isPresent());
        Assertions.assertEquals(aCoupon.getId().getValue(), aOutput.get().getId().getValue());
        Assertions.assertEquals(aCode, aOutput.get().getCode().getValue());
        Assertions.assertEquals(aPercentage, aOutput.get().getPercentage());
        Assertions.assertEquals(aExpirationDate, aOutput.get().getExpirationDate());
        Assertions.assertEquals(aIsActive, aOutput.get().isActive());
        Assertions.assertEquals(aType, aOutput.get().getType());
        Assertions.assertNotNull(aOutput.get().getCreatedAt());
        Assertions.assertNotNull(aOutput.get().getUpdatedAt());
    }

    @Test
    void givenAnInvalidCode_whenCallFindByCode_thenShouldReturnEmpty() {
        final var aOutput = this.couponGateway.findByCode("invalid-code");
        Assertions.assertTrue(aOutput.isEmpty());
    }

    @Test
    void givenAValidQuery_whenCallFindAll_shouldReturnAPaginationOfCoupons() {
        final var aCouponLimited = Fixture.Coupons.limitedCouponActivated();
        final var aCouponUnlimited = Coupon.newCoupon(
                "Z_FREE_SHIPPING50",
                50.0f,
                InstantUtils.now().plus(1, ChronoUnit.DAYS),
                true,
                CouponType.UNLIMITED
        );
        final var aCoupons = List.of(aCouponLimited, aCouponUnlimited);

        this.couponJpaRepository.saveAll(aCoupons.stream().map(CouponJpaEntity::toEntity).toList());

        final var aPage = 0;
        final var aPerPage = 1;
        final var aTotalPages = 2;
        final var aTotalItems = 2;

        Assertions.assertEquals(2, this.couponJpaRepository.count());

        final var aQuery = new SearchQuery(0, 1, "", "code", "ASC");
        final var actualResult = this.couponGateway.findAll(aQuery);

        Assertions.assertEquals(aPage, actualResult.currentPage());
        Assertions.assertEquals(aPerPage, actualResult.perPage());
        Assertions.assertEquals(aTotalPages, actualResult.totalPages());
        Assertions.assertEquals(aTotalItems, actualResult.totalItems());
        Assertions.assertEquals(aPerPage, actualResult.items().size());
        Assertions.assertEquals(aCouponLimited.getCode().getValue(), actualResult.items().get(0).getCode().getValue());
    }

    @Test
    void givenAValidQueryButHasNoData_whenCallFindAll_shouldReturnEmptyCoupons() {
        final var aPage = 0;
        final var aPerPage = 1;
        final var aTotalPages = 0;
        final var aTotalItems = 0;

        final var aQuery = new SearchQuery(aPage, aPerPage, "", "code", "ASC");
        final var actualResult = this.couponGateway.findAll(aQuery);

        Assertions.assertEquals(aPage, actualResult.currentPage());
        Assertions.assertEquals(aPerPage, actualResult.perPage());
        Assertions.assertEquals(aTotalPages, actualResult.totalPages());
        Assertions.assertEquals(aTotalItems, actualResult.totalItems());
        Assertions.assertEquals(0, actualResult.items().size());
    }

    @Test
    void givenAValidQueryAndTerms_whenCallFindAll_shouldReturnAPaginationOfCoupons() {
        final var aCouponLimited = Fixture.Coupons.limitedCouponActivated();
        final var aCouponUnlimited = Coupon.newCoupon(
                "Z_FREE_SHIPPING50",
                50.0f,
                InstantUtils.now().plus(1, ChronoUnit.DAYS),
                true,
                CouponType.UNLIMITED
        );
        final var aCoupons = List.of(aCouponLimited, aCouponUnlimited);

        this.couponJpaRepository.saveAll(aCoupons.stream().map(CouponJpaEntity::toEntity).toList());

        final var aPage = 0;
        final var aPerPage = 1;
        final var aTotalPages = 1;
        final var aTotalItems = 1;

        Assertions.assertEquals(2, this.couponJpaRepository.count());

        final var aQuery = new SearchQuery(0, 1, "Z", "code", "ASC");
        final var actualResult = this.couponGateway.findAll(aQuery);

        Assertions.assertEquals(aPage, actualResult.currentPage());
        Assertions.assertEquals(aPerPage, actualResult.perPage());
        Assertions.assertEquals(aTotalPages, actualResult.totalPages());
        Assertions.assertEquals(aTotalItems, actualResult.totalItems());
        Assertions.assertEquals(aPerPage, actualResult.items().size());
        Assertions.assertEquals(aCouponUnlimited.getCode().getValue(), actualResult.items().get(0).getCode().getValue());
    }
}
