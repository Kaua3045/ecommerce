package com.kaua.ecommerce.domain.order.validations;

import com.kaua.ecommerce.domain.order.OrderDelivery;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.ValidationHandler;
import com.kaua.ecommerce.domain.validation.Validator;

public class OrderDeliveryValidation extends Validator {

    private final OrderDelivery orderDelivery;

    public OrderDeliveryValidation(final ValidationHandler handler, final OrderDelivery orderDelivery) {
        super(handler);
        this.orderDelivery = orderDelivery;
    }

    @Override
    public void validate() {
        checkFreightTypeConstraints();
        checkFreightPriceConstraints();
        checkDeliveryEstimatedConstraints();
        checkStreetConstraints();
        checkNumberConstraints();
        checkComplementConstraints();
        checkDistrictConstraints();
        checkCityConstraints();
        checkStateConstraints();
        checkZipCodeConstraints();
    }

    private void checkFreightTypeConstraints() {
        if (this.orderDelivery.getFreightType() == null || this.orderDelivery.getFreightType().isBlank()) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullOrBlank("freightType")));
        }
    }

    private void checkFreightPriceConstraints() {
        if (this.orderDelivery.getFreightPrice() <= 0) {
            this.validationHandler().append(new Error(CommonErrorMessage.greaterThan("freightPrice", 0)));
        }
    }

    private void checkDeliveryEstimatedConstraints() {
        if (this.orderDelivery.getDeliveryEstimated() <= 0) {
            this.validationHandler().append(new Error(CommonErrorMessage.greaterThan("deliveryEstimated", 0)));
        }
    }

    private void checkStreetConstraints() {
        if (this.orderDelivery.getStreet() == null || this.orderDelivery.getStreet().isBlank()) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullOrBlank("street")));
        }
    }

    private void checkNumberConstraints() {
        if (this.orderDelivery.getNumber() == null || this.orderDelivery.getNumber().isBlank()) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullOrBlank("number")));
        }
    }

    private void checkComplementConstraints() {
        if (this.orderDelivery.getComplement().isPresent() && this.orderDelivery.getComplement().get().isBlank()) {
            this.validationHandler().append(new Error(CommonErrorMessage.blankMessage("complement")));
        }
    }

    private void checkDistrictConstraints() {
        if (this.orderDelivery.getDistrict() == null || this.orderDelivery.getDistrict().isBlank()) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullOrBlank("district")));
        }
    }

    private void checkCityConstraints() {
        if (this.orderDelivery.getCity() == null || this.orderDelivery.getCity().isBlank()) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullOrBlank("city")));
        }
    }

    private void checkStateConstraints() {
        if (this.orderDelivery.getState() == null || this.orderDelivery.getState().isBlank()) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullOrBlank("state")));
        }
    }

    private void checkZipCodeConstraints() {
        if (this.orderDelivery.getZipCode() == null || this.orderDelivery.getZipCode().isBlank()) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullOrBlank("zipCode")));
        }
    }
}
