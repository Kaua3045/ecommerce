package com.kaua.ecommerce.domain.coupon;

import com.kaua.ecommerce.domain.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CouponSlotTest extends UnitTest {

    @Test
    void givenAValidValues_whenCallNewCouponSlot_thenCouponSlotShouldBeCreated() {
        final var aCouponID = "123456789";
        final var aCouponSlot = CouponSlot.newCouponSlot(aCouponID);

        Assertions.assertNotNull(aCouponSlot);
        Assertions.assertNotNull(aCouponSlot.getSlotId());
        Assertions.assertEquals(aCouponID, aCouponSlot.getCouponId());
    }

    @Test
    void givenAValidValues_whenCallWith_thenCouponSlotShouldBeCreated() {
        final var aCouponSlotID = "123456789";
        final var aCouponID = "123456789";
        final var aCouponSlot = CouponSlot.with(aCouponSlotID, aCouponID);

        Assertions.assertNotNull(aCouponSlot);
        Assertions.assertEquals(aCouponSlotID, aCouponSlot.getSlotId());
        Assertions.assertEquals(aCouponID, aCouponSlot.getCouponId());
    }

    @Test
    void givenANullCouponID_whenCallNewCouponSlot_shouldReturnDomainException() {
        final String aCouponID = null;

        final var expectedErrorMessage = "'couponId' should not be null";

        final var aOutput = Assertions.assertThrows(NullPointerException.class,
                () -> CouponSlot.newCouponSlot(aCouponID));

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());
    }

    @Test
    void givenANullCouponID_whenCallWith_shouldReturnDomainException() {
        final String aCouponSlotID = null;
        final var aCouponID = "123456789";

        final var expectedErrorMessage = "'slotId' should not be null";

        final var aOutput = Assertions.assertThrows(NullPointerException.class,
                () -> CouponSlot.with(aCouponSlotID, aCouponID));

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());
    }
}
