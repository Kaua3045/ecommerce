package com.kaua.ecommerce.domain.coupon;

import com.kaua.ecommerce.domain.TestValidationHandler;
import com.kaua.ecommerce.domain.UnitTest;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.utils.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class CouponValidationTest extends UnitTest {

    @Test
    void givenInvalidBlankCode_whenCallNewCoupon_shouldReturnDomainException() {
        final var aCode = " ";
        final var aPercentage = 10.0f;
        final var aExpirationDate = InstantUtils.now().plus(1, ChronoUnit.DAYS);
        final var aIsActive = true;
        final var aType = CouponType.UNLIMITED;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("code");
        final var expectedErrorCount = 1;

        final var aCoupon = Coupon.newCoupon(aCode, aPercentage, aExpirationDate, aIsActive, aType);

        final var aTestValidationHandler = new TestValidationHandler();
        aCoupon.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNullCode_whenCallNewCoupon_shouldReturnDomainException() {
        final String aCode = null;
        final var aPercentage = 10.0f;
        final var aExpirationDate = InstantUtils.now().plus(1, ChronoUnit.DAYS);
        final var aIsActive = true;
        final var aType = CouponType.UNLIMITED;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("code");
        final var expectedErrorCount = 1;

        final var aCoupon = Coupon.newCoupon(aCode, aPercentage, aExpirationDate, aIsActive, aType);

        final var aTestValidationHandler = new TestValidationHandler();
        aCoupon.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidCodeMoreThan100Characters_whenCallNewCoupon_shouldReturnDomainException() {
        final var aCode = RandomStringUtils.generateValue(101);
        final var aPercentage = 10.0f;
        final var aExpirationDate = InstantUtils.now().plus(1, ChronoUnit.DAYS);
        final var aIsActive = true;
        final var aType = CouponType.UNLIMITED;

        final var expectedErrorMessage = CommonErrorMessage.maxSize("code", 100);
        final var expectedErrorCount = 1;

        final var aCoupon = Coupon.newCoupon(aCode, aPercentage, aExpirationDate, aIsActive, aType);

        final var aTestValidationHandler = new TestValidationHandler();
        aCoupon.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNegativePercentage_whenCallNewCoupon_shouldReturnDomainException() {
        final var aCode = "BLACKFRIDAY";
        final var aPercentage = -10.0f;
        final var aExpirationDate = InstantUtils.now().plus(1, ChronoUnit.DAYS);
        final var aIsActive = true;
        final var aType = CouponType.UNLIMITED;

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("percentage", 0);
        final var expectedErrorCount = 1;

        final var aCoupon = Coupon.newCoupon(aCode, aPercentage, aExpirationDate, aIsActive, aType);

        final var aTestValidationHandler = new TestValidationHandler();
        aCoupon.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNullExpirationDate_whenCallNewCoupon_shouldReturnDomainException() {
        final var aCode = "BLACKFRIDAY";
        final var aPercentage = 10.0f;
        final Instant aExpirationDate = null;
        final var aIsActive = true;
        final var aType = CouponType.UNLIMITED;

        final var expectedErrorMessage = CommonErrorMessage.nullMessage("expirationDate");
        final var expectedErrorCount = 1;

        final var aCoupon = Coupon.newCoupon(aCode, aPercentage, aExpirationDate, aIsActive, aType);

        final var aTestValidationHandler = new TestValidationHandler();
        aCoupon.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidExpirationDateInThePast_whenCallNewCoupon_shouldReturnDomainException() {
        final var aCode = "BLACKFRIDAY";
        final var aPercentage = 10.0f;
        final var aExpirationDate = InstantUtils.now().minus(1, ChronoUnit.DAYS);
        final var aIsActive = true;
        final var aType = CouponType.UNLIMITED;

        final var expectedErrorMessage = CommonErrorMessage.dateMustBeFuture("expirationDate");
        final var expectedErrorCount = 1;

        final var aCoupon = Coupon.newCoupon(aCode, aPercentage, aExpirationDate, aIsActive, aType);

        final var aTestValidationHandler = new TestValidationHandler();
        aCoupon.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNullType_whenCallNewCoupon_shouldReturnDomainException() {
        final var aCode = "BLACKFRIDAY";
        final var aPercentage = 10.0f;
        final var aExpirationDate = InstantUtils.now().plus(1, ChronoUnit.DAYS);
        final var aIsActive = true;
        final CouponType aType = null;

        final var expectedErrorMessage = CommonErrorMessage.nullMessage("type");
        final var expectedErrorCount = 1;

        final var aCoupon = Coupon.newCoupon(aCode, aPercentage, aExpirationDate, aIsActive, aType);

        final var aTestValidationHandler = new TestValidationHandler();
        aCoupon.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidCode_whenCallUpdate_shouldReturnDomainException() {
        final var aCode = " ";
        final var aPercentage = 10.0f;
        final var aExpirationDate = InstantUtils.now().plus(1, ChronoUnit.DAYS);

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("code");
        final var expectedErrorCount = 1;

        final var aCoupon = Coupon.newCoupon("BLACKFRIDAY", 10.0f, InstantUtils.now().plus(1, ChronoUnit.DAYS), true, CouponType.UNLIMITED);

        final var aCouponUpdated = aCoupon.update(aCode, aPercentage, aExpirationDate);

        final var aTestValidationHandler = new TestValidationHandler();
        aCouponUpdated.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNegativePercentage_whenCallUpdate_shouldReturnDomainException() {
        final var aCode = "BLACKFRIDAY";
        final var aPercentage = -10.0f;
        final var aExpirationDate = InstantUtils.now().plus(1, ChronoUnit.DAYS);

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("percentage", 0);
        final var expectedErrorCount = 1;

        final var aCoupon = Coupon.newCoupon("BLACKFRIDAY", 10.0f, InstantUtils.now().plus(1, ChronoUnit.DAYS), true, CouponType.UNLIMITED);

        final var aCouponUpdated = aCoupon.update(aCode, aPercentage, aExpirationDate);

        final var aTestValidationHandler = new TestValidationHandler();
        aCouponUpdated.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNullExpirationDate_whenCallUpdate_shouldReturnDomainException() {
        final var aCode = "BLACKFRIDAY";
        final var aPercentage = 10.0f;
        final Instant aExpirationDate = null;

        final var expectedErrorMessage = CommonErrorMessage.nullMessage("expirationDate");
        final var expectedErrorCount = 1;

        final var aCoupon = Coupon.newCoupon("BLACKFRIDAY", 10.0f, InstantUtils.now().plus(1, ChronoUnit.DAYS), true, CouponType.UNLIMITED);

        final var aCouponUpdated = aCoupon.update(aCode, aPercentage, aExpirationDate);

        final var aTestValidationHandler = new TestValidationHandler();
        aCouponUpdated.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidExpirationDateInThePast_whenCallUpdate_shouldReturnDomainException() {
        final var aCode = "BLACKFRIDAY";
        final var aPercentage = 10.0f;
        final var aExpirationDate = InstantUtils.now().minus(1, ChronoUnit.DAYS);

        final var expectedErrorMessage = CommonErrorMessage.dateMustBeFuture("expirationDate");
        final var expectedErrorCount = 1;

        final var aCoupon = Coupon.newCoupon("BLACKFRIDAY", 10.0f, InstantUtils.now().plus(1, ChronoUnit.DAYS), true, CouponType.UNLIMITED);

        final var aCouponUpdated = aCoupon.update(aCode, aPercentage, aExpirationDate);

        final var aTestValidationHandler = new TestValidationHandler();
        aCouponUpdated.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }
}
