package com.kaua.ecommerce.application.exceptions;

import com.kaua.ecommerce.domain.exceptions.DomainException;

import java.util.Collections;

public class CouponMinimumPurchaseAmountException extends DomainException {

    public CouponMinimumPurchaseAmountException() {
        super("Coupon minimum purchase amount is not reached", Collections.emptyList());
    }
}
