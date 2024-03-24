package com.kaua.ecommerce.domain.order.validations;

import com.kaua.ecommerce.domain.order.Order;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.ValidationHandler;
import com.kaua.ecommerce.domain.validation.Validator;

import java.math.BigDecimal;

public class OrderValidation extends Validator {

    private final Order order;

    public OrderValidation(final ValidationHandler handler, final Order order) {
        super(handler);
        this.order = order;
    }

    @Override
    public void validate() {
        checkCustomerIdConstraints();
        checkCouponCodeConstraints();
        checkCouponPercentageConstraints();
        checkOrderPaymentIdConstraints();
        checkOrderItemsConstraints();
        checkOrderTotalAmountConstraints();
    }

    private void checkCustomerIdConstraints() {
        if (this.order.getCustomerId() == null || this.order.getCustomerId().isBlank()) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullOrBlank("customerId")));
        }
    }

    private void checkCouponCodeConstraints() {
        if (this.order.getCouponCode().isPresent() && this.order.getCouponCode().get().isBlank()) {
            this.validationHandler().append(new Error(CommonErrorMessage.blankMessage("couponCode")));
        }
    }

    private void checkCouponPercentageConstraints() {
        if (this.order.getCouponPercentage() < 0) {
            this.validationHandler().append(new Error(CommonErrorMessage.greaterThan("couponPercentage", -1)));
        }
    }

    private void checkOrderPaymentIdConstraints() {
        if (this.order.getOrderPaymentId() == null) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullMessage("orderPaymentId")));
        }
    }

    private void checkOrderItemsConstraints() {
        if (this.order.getOrderItems().isEmpty()) {
            this.validationHandler().append(new Error(CommonErrorMessage.minSize("orderItems", 1)));
        }
    }

    private void checkOrderTotalAmountConstraints() {
        if (this.order.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            this.validationHandler().append(new Error(CommonErrorMessage.greaterThan("totalAmount", 0)));
        }
    }
}
