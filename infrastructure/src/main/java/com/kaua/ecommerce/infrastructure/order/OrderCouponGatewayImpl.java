package com.kaua.ecommerce.infrastructure.order;

import com.kaua.ecommerce.application.gateways.order.OrderCouponGateway;
import com.kaua.ecommerce.application.usecases.coupon.apply.ApplyCouponCommand;
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
        // TODO - Implement this method to receive total amount from order to validate coupon and apply it
        final var aCommand = ApplyCouponCommand.with(couponCode, 100f);
        final var aOutput = this.applyCouponUseCase.execute(aCommand);
        return new OrderCouponApplyOutput(
                aOutput.couponId(),
                aOutput.couponCode(),
                aOutput.couponPercentage()
        );
    }
}
