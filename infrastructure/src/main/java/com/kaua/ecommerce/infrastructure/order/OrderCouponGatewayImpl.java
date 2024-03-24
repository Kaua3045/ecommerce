package com.kaua.ecommerce.infrastructure.order;

import com.kaua.ecommerce.application.gateways.order.OrderCouponGateway;
import com.kaua.ecommerce.application.usecases.coupon.slot.remove.RemoveCouponSlotUseCase;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class OrderCouponGatewayImpl implements OrderCouponGateway {

    private final RemoveCouponSlotUseCase removeCouponSlotUseCase;

    public OrderCouponGatewayImpl(final RemoveCouponSlotUseCase removeCouponSlotUseCase) {
        this.removeCouponSlotUseCase = Objects.requireNonNull(removeCouponSlotUseCase);
    }

    @Override
    public OrderCouponApplyOutput applyCoupon(String couponCode) {
        final var aOutput = this.removeCouponSlotUseCase.execute(couponCode);
        return new OrderCouponApplyOutput(
                aOutput.couponId(),
                aOutput.couponCode(),
                aOutput.couponPercentage()
        );
    }
}
