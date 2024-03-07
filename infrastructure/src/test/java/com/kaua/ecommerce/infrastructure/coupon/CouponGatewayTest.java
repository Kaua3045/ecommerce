package com.kaua.ecommerce.infrastructure.coupon;

import com.kaua.ecommerce.domain.coupon.Coupon;
import com.kaua.ecommerce.domain.coupon.CouponType;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.infrastructure.DatabaseGatewayTest;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
}
