package com.kaua.ecommerce.domain.customer;

import com.kaua.ecommerce.domain.ValueObject;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.validation.Error;

public class Telephone extends ValueObject {

    private String value;

    private Telephone(final String value) {
        this.value = value;
        selfValidation();
    }

    public static Telephone newTelephone(final String value) {
        return new Telephone(value);
    }

    private void selfValidation() {
        if (this.value == null || this.value.isEmpty()) {
            throw DomainException.with(new Error(CommonErrorMessage.nullOrBlank("telephone")));
        }
    }

    public String getValue() {
        return value;
    }
}
