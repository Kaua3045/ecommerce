package com.kaua.ecommerce.domain.customer.address;

import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.ValidationHandler;
import com.kaua.ecommerce.domain.validation.Validator;

public class AddressValidation extends Validator {

    private final Address address;

    protected AddressValidation(final Address address, final ValidationHandler handler) {
        super(handler);
        this.address = address;
    }

    @Override
    public void validate() {
        if (this.address.getStreet() == null || this.address.getStreet().isBlank()) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullOrBlank("street")));
        }

        if (this.address.getNumber() == null || this.address.getNumber().isBlank()) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullOrBlank("number")));
        }

        if (this.address.getDistrict() == null || this.address.getDistrict().isBlank()) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullOrBlank("district")));
        }

        if (this.address.getZipCode() == null || this.address.getZipCode().isBlank()) {
            this.validationHandler().append(new Error(CommonErrorMessage.nullOrBlank("zipCode")));
        }
    }
}
