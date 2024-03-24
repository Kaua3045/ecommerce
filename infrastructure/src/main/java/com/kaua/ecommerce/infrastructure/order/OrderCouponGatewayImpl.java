package com.kaua.ecommerce.infrastructure.order;

import com.kaua.ecommerce.application.gateways.order.OrderCouponGateway;
import com.kaua.ecommerce.application.usecases.coupon.apply.ApplyCouponUseCase;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class OrderCouponGatewayImpl implements OrderCouponGateway {

    private final ApplyCouponUseCase applyCouponUseCase;

    public OrderCouponGatewayImpl(final ApplyCouponUseCase applyCouponUseCase) {
        this.applyCouponUseCase = Objects.requireNonNull(applyCouponUseCase);
    }

    @Override
    public OrderCouponApplyOutput applyCoupon(String couponCode) {
        final var aOutput = this.applyCouponUseCase.execute(couponCode);
        return new OrderCouponApplyOutput(
                aOutput.couponId(),
                aOutput.couponCode(),
                aOutput.couponPercentage()
        );
    }
}
