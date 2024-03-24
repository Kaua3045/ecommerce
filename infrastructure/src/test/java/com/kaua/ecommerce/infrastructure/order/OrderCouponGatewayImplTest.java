package com.kaua.ecommerce.infrastructure.order;

import com.kaua.ecommerce.application.exceptions.CouponNoMoreAvailableException;
import com.kaua.ecommerce.application.usecases.coupon.apply.ApplyCouponOutput;
import com.kaua.ecommerce.application.usecases.coupon.apply.ApplyCouponUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@IntegrationTest
public class OrderCouponGatewayImplTest {

    @Autowired
    private OrderCouponGatewayImpl orderCouponGatewayImpl;

    @MockBean
    private ApplyCouponUseCase applyCouponUseCase;

    @Test
    void givenAValidCouponCode_whenCallApplyCoupon_thenCouponIsApplied() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();

        Mockito.when(applyCouponUseCase.execute(aCoupon.getCode().getValue()))
                .thenReturn(ApplyCouponOutput.from(aCoupon));

        final var aOutput = this.orderCouponGatewayImpl.applyCoupon(aCoupon.getCode().getValue());

        Assertions.assertEquals(aCoupon.getId().getValue(), aOutput.couponId());
        Assertions.assertEquals(aCoupon.getCode().getValue(), aOutput.couponCode());
        Assertions.assertEquals(aCoupon.getPercentage(), aOutput.couponPercentage());
    }

    @Test
    void givenAValidCouponCode_whenCallApplyCouponButNoMoreSlot_thenThrowCouponNoMoreAvailableException() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();

        Mockito.when(applyCouponUseCase.execute(aCoupon.getCode().getValue()))
                .thenThrow(new CouponNoMoreAvailableException());

        Assertions.assertThrows(CouponNoMoreAvailableException.class, () ->
                this.orderCouponGatewayImpl.applyCoupon(aCoupon.getCode().getValue()));
    }
}
