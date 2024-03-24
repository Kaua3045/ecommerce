package com.kaua.ecommerce.application.usecases.order.create;

import java.util.Optional;
import java.util.Set;

public record CreateOrderCommand(
        String customerId,
        String couponCode,
        String freightType,
        String paymentMethodId,
        int installments,
        Set<CreateOrderItemsCommand> items
) {

    public static CreateOrderCommand with(
            final String customerId,
            final String couponCode,
            final String freightType,
            final String paymentMethodId,
            final int installments,
            final Set<CreateOrderItemsCommand> items
    ) {
        return new CreateOrderCommand(
                customerId,
                couponCode,
                freightType,
                paymentMethodId,
                installments,
                items
        );
    }

    public Optional<String> getCouponCode() {
        return couponCode == null || couponCode.isBlank() ? Optional.empty() : Optional.of(couponCode);
    }
}
