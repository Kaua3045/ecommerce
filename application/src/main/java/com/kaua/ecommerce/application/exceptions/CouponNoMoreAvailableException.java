package com.kaua.ecommerce.application.exceptions;

import com.kaua.ecommerce.domain.exceptions.DomainException;

import java.util.Collections;

public class CouponNoMoreAvailableException extends DomainException {

    public CouponNoMoreAvailableException() {
        super("Coupon no more available", Collections.emptyList());
    }
}
