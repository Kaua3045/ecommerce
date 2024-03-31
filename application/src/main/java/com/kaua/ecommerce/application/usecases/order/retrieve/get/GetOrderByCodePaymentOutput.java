package com.kaua.ecommerce.application.usecases.order.retrieve.get;

import com.kaua.ecommerce.domain.order.OrderPayment;

public record GetOrderByCodePaymentOutput(
        String orderPaymentId,
        String paymentMethodId,
        int installments
) {

    public static GetOrderByCodePaymentOutput from(final OrderPayment aOrderPayment) {
        return new GetOrderByCodePaymentOutput(
                aOrderPayment.getId().getValue(),
                aOrderPayment.getPaymentMethodId(),
                aOrderPayment.getInstallments()
        );
    }
}
