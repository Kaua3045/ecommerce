package com.kaua.ecommerce.domain.coupon;

import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.ValidationHandler;
import com.kaua.ecommerce.domain.validation.Validator;

public class CouponValidation extends Validator {

    private final Coupon coupon;

    public CouponValidation(final ValidationHandler handler, final Coupon coupon) {
        super(handler);
        this.coupon = coupon;
    }

    @Override
    public void validate() {
        this.checkCodeConstraints();
        this.checkPercentageConstraints();
        this.checkExpirationDateConstraints();
        this.checkTypeConstraints();
    }

    private void checkCodeConstraints() {
        if (this.coupon.getCode() == null || this.coupon.getCode().isBlank()) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullOrBlank("code")));
            return;
        }

        if (this.coupon.getCode().length() > 100) {
            this.validationHandler().append(new Error(CommonErrorMessage.maxSize("code", 100)));
        }
    }

    private void checkPercentageConstraints() {
        if (this.coupon.getPercentage() < 0) {
            this.validationHandler().append(new Error(CommonErrorMessage.greaterThan("percentage", 0)));
        }
    }

    private void checkExpirationDateConstraints() {
        if (this.coupon.getExpirationDate() == null) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullMessage("expirationDate")));
            return;
        }

        if (this.coupon.getExpirationDate().isBefore(InstantUtils.now())) {
            this.validationHandler().append(new Error(CommonErrorMessage.dateMustBeFuture("expirationDate")));
        }
    }

    private void checkTypeConstraints() {
        if (this.coupon.getType() == null) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullMessage("type")));
        }
    }
}
