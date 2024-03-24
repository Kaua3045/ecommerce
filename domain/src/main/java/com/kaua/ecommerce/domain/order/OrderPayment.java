package com.kaua.ecommerce.domain.order;

import com.kaua.ecommerce.domain.AggregateRoot;
import com.kaua.ecommerce.domain.order.identifiers.OrderPaymentID;
import com.kaua.ecommerce.domain.order.validations.OrderPaymentValidation;
import com.kaua.ecommerce.domain.validation.ValidationHandler;

public class OrderPayment extends AggregateRoot<OrderPaymentID> {

    private final String paymentMethodId;
    private final int installments;

    private OrderPayment(
            final OrderPaymentID aOrderPaymentId,
            final String aPaymentMethodId,
            final int aInstallments
    ) {
        super(aOrderPaymentId);
        this.paymentMethodId = aPaymentMethodId;
        this.installments = aInstallments;
    }

    public static OrderPayment newOrderPayment(
            final String aPaymentMethodId,
            final int aInstallments
    ) {
        final var aOrderPaymentId = OrderPaymentID.unique();
        return new OrderPayment(
                aOrderPaymentId,
                aPaymentMethodId,
                aInstallments
        );
    }

    public static OrderPayment with(
            final String aOrderPaymentId,
            final String aPaymentMethodId,
            final int aInstallments
    ) {
        return new OrderPayment(
                OrderPaymentID.from(aOrderPaymentId),
                aPaymentMethodId,
                aInstallments
        );
    }

    @Override
    public void validate(ValidationHandler handler) {
        new OrderPaymentValidation(handler, this).validate();
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public int getInstallments() {
        return installments;
    }
}
