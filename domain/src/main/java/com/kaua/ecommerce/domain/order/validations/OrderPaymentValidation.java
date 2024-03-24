package com.kaua.ecommerce.domain.order.validations;

import com.kaua.ecommerce.domain.order.OrderPayment;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.ValidationHandler;
import com.kaua.ecommerce.domain.validation.Validator;

public class OrderPaymentValidation extends Validator {

    private final OrderPayment orderPayment;

    public OrderPaymentValidation(final ValidationHandler handler, final OrderPayment orderPayment) {
        super(handler);
        this.orderPayment = orderPayment;
    }

    @Override
    public void validate() {
        checkPaymentMethodIdConstraints();
        checkInstallmentsConstraints();
    }

    private void checkPaymentMethodIdConstraints() {
        if (this.orderPayment.getPaymentMethodId() == null || this.orderPayment.getPaymentMethodId().isBlank()) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullOrBlank("paymentMethodId")));
        }
    }

    private void checkInstallmentsConstraints() {
        if (this.orderPayment.getInstallments() < 0) {
            this.validationHandler().append(new Error(CommonErrorMessage.greaterThan("installments", -1)));
        }
    }
}
