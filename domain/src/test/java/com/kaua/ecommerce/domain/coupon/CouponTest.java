package com.kaua.ecommerce.domain.coupon;

import com.kaua.ecommerce.domain.UnitTest;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.temporal.ChronoUnit;

public class CouponTest extends UnitTest {

    @Test
    void givenAValidValues_whenCreateNewCoupon_thenCouponShouldBeCreated() {
        final var aCode = "BLACKFRIDAY";
        final var aPercentage = 10.0f;
        final var aExpirationDate = InstantUtils.now().plus(1, ChronoUnit.DAYS);
        final var aIsActive = true;
        final var aType = CouponType.UNLIMITED;

        final var aCoupon = Coupon.newCoupon(aCode, aPercentage, aExpirationDate, aIsActive, aType);

        Assertions.assertNotNull(aCoupon);
        Assertions.assertNotNull(aCoupon.getId());
        Assertions.assertEquals(aCode, aCoupon.getCode());
        Assertions.assertEquals(aPercentage, aCoupon.getPercentage());
        Assertions.assertEquals(aExpirationDate, aCoupon.getExpirationDate());
        Assertions.assertEquals(aIsActive, aCoupon.isActive());
        Assertions.assertEquals(aType, aCoupon.getType());
        Assertions.assertNotNull(aCoupon.getCreatedAt());
        Assertions.assertNotNull(aCoupon.getUpdatedAt());
        Assertions.assertDoesNotThrow(() -> aCoupon.validate(new ThrowsValidationHandler()));
    }

    @Test
    void testCouponIdEqualsAndHashCode() {
        final var aCouponId = CouponID.from("123456789");
        final var anotherCouponId = CouponID.from("123456789");

        Assertions.assertTrue(aCouponId.equals(anotherCouponId));
        Assertions.assertTrue(aCouponId.equals(aCouponId));
        Assertions.assertFalse(aCouponId.equals(null));
        Assertions.assertFalse(aCouponId.equals(""));
        Assertions.assertEquals(aCouponId.hashCode(), anotherCouponId.hashCode());
    }

    @Test
    void givenAValidTypeName_whenCallCouponTypeOf_thenCouponTypeShouldBeReturned() {
        final var aTypeName = "UNLIMITED";
        final var aCouponType = CouponType.of(aTypeName);

        Assertions.assertNotNull(aCouponType);
        Assertions.assertEquals(aTypeName, aCouponType.get().name());
    }

    @Test
    void givenAValidValues_whenCallWith_thenCouponShouldBeCreated() {
        final var aCouponID = "123456789";
        final var aCode = "BLACKFRIDAY";
        final var aPercentage = 10.0f;
        final var aExpirationDate = InstantUtils.now().plus(1, ChronoUnit.DAYS);
        final var aIsActive = true;
        final var aType = CouponType.UNLIMITED;
        final var aCreatedAt = InstantUtils.now();
        final var aUpdatedAt = InstantUtils.now();

        final var aCoupon = Coupon.with(aCouponID, aCode, aPercentage, aExpirationDate, aIsActive, aType, aCreatedAt, aUpdatedAt, 0);

        Assertions.assertNotNull(aCoupon);
        Assertions.assertNotNull(aCoupon.getId());
        Assertions.assertEquals(aCouponID, aCoupon.getId().getValue());
        Assertions.assertEquals(aCode, aCoupon.getCode());
        Assertions.assertEquals(aPercentage, aCoupon.getPercentage());
        Assertions.assertEquals(aExpirationDate, aCoupon.getExpirationDate());
        Assertions.assertEquals(aIsActive, aCoupon.isActive());
        Assertions.assertEquals(aType, aCoupon.getType());
        Assertions.assertEquals(aCreatedAt, aCoupon.getCreatedAt());
        Assertions.assertEquals(aUpdatedAt, aCoupon.getUpdatedAt());
    }

    @Test
    void givenAValidCoupon_whenCallWith_thenShouldReturnCoupon() {
        final var aCode = "BLACKFRIDAY";
        final var aPercentage = 10.0f;
        final var aExpirationDate = InstantUtils.now().plus(1, ChronoUnit.DAYS);
        final var aIsActive = true;
        final var aType = CouponType.UNLIMITED;

        final var aCoupon = Coupon.newCoupon(aCode, aPercentage, aExpirationDate, aIsActive, aType);

        final var aCouponWith = Coupon.with(aCoupon);

        Assertions.assertNotNull(aCouponWith);
        Assertions.assertNotNull(aCouponWith.getId());
        Assertions.assertEquals(aCoupon.getId().getValue(), aCouponWith.getId().getValue());
        Assertions.assertEquals(aCode, aCouponWith.getCode());
        Assertions.assertEquals(aPercentage, aCouponWith.getPercentage());
        Assertions.assertEquals(aExpirationDate, aCouponWith.getExpirationDate());
        Assertions.assertEquals(aIsActive, aCouponWith.isActive());
        Assertions.assertEquals(aType, aCouponWith.getType());
        Assertions.assertEquals(aCoupon.getCreatedAt(), aCouponWith.getCreatedAt());
        Assertions.assertEquals(aCoupon.getUpdatedAt(), aCouponWith.getUpdatedAt());
    }

    @Test
    void givenAValidCoupon_whenCallIsExpired_thenShouldReturnFalse() {
        final var aCode = "BLACKFRIDAY";
        final var aPercentage = 10.0f;
        final var aExpirationDate = InstantUtils.now().plus(1, ChronoUnit.DAYS);
        final var aIsActive = true;
        final var aType = CouponType.UNLIMITED;

        final var aCoupon = Coupon.newCoupon(aCode, aPercentage, aExpirationDate, aIsActive, aType);

        Assertions.assertFalse(aCoupon.isExpired());
    }

    @Test
    void givenAValidCoupon_whenCallIsActiveAndNotExpired_thenShouldReturnTrue() {
        final var aCode = "BLACKFRIDAY";
        final var aPercentage = 10.0f;
        final var aExpirationDate = InstantUtils.now().plus(1, ChronoUnit.DAYS);
        final var aIsActive = true;
        final var aType = CouponType.UNLIMITED;

        final var aCoupon = Coupon.newCoupon(aCode, aPercentage, aExpirationDate, aIsActive, aType);

        Assertions.assertTrue(aCoupon.isActiveAndNotExpired());
    }

    @Test
    void givenAValidCouponInactivated_whenCallIsActiveAndNotExpired_thenShouldReturnFalse() {
        final var aCode = "BLACKFRIDAY";
        final var aPercentage = 10.0f;
        final var aExpirationDate = InstantUtils.now().plus(1, ChronoUnit.DAYS);
        final var aIsActive = false;
        final var aType = CouponType.UNLIMITED;

        final var aCoupon = Coupon.newCoupon(aCode, aPercentage, aExpirationDate, aIsActive, aType);

        Assertions.assertFalse(aCoupon.isActiveAndNotExpired());
    }

    @Test
    void givenAValidCouponExpired_whenCallIsActiveAndNotExpired_thenShouldReturnFalse() {
        final var aCode = "BLACKFRIDAY";
        final var aPercentage = 10.0f;
        final var aExpirationDate = InstantUtils.now().minus(1, ChronoUnit.DAYS);
        final var aIsActive = true;
        final var aType = CouponType.UNLIMITED;

        final var aCoupon = Coupon.newCoupon(aCode, aPercentage, aExpirationDate, aIsActive, aType);

        Assertions.assertFalse(aCoupon.isActiveAndNotExpired());
    }

    @Test
    void givenAValidCoupon_whenCallGetDiscount_thenShouldReturnDiscount() {
        final var aCode = "BLACKFRIDAY";
        final var aPercentage = 10.0f;
        final var aExpirationDate = InstantUtils.now().plus(1, ChronoUnit.DAYS);
        final var aIsActive = true;
        final var aType = CouponType.UNLIMITED;

        final var aCoupon = Coupon.newCoupon(aCode, aPercentage, aExpirationDate, aIsActive, aType);

        final var total = 100.0;
        final var expectedDiscount = 10.0;

        Assertions.assertEquals(expectedDiscount, aCoupon.getDiscount(total));
    }

    @Test
    void givenAValidCoupon_whenCallToString_thenShouldReturnString() {
        final var aCode = "BLACKFRIDAY";
        final var aPercentage = 10.0f;
        final var aExpirationDate = InstantUtils.now().plus(1, ChronoUnit.DAYS);
        final var aIsActive = true;
        final var aType = CouponType.UNLIMITED;

        final var aCoupon = Coupon.newCoupon(aCode, aPercentage, aExpirationDate, aIsActive, aType);

        final var expectedToString = "Coupon(id='" + aCoupon.getId().getValue() + "', code='BLACKFRIDAY', percentage=10.0, expirationDate=" + aExpirationDate + ", isActive=true, type=UNLIMITED, createdAt=" + aCoupon.getCreatedAt() + ", updatedAt=" + aCoupon.getUpdatedAt() + ", version=0)";

        Assertions.assertEquals(expectedToString, aCoupon.toString());
    }
}
