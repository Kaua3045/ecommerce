package com.kaua.ecommerce.domain.customer;

import com.kaua.ecommerce.domain.ValueObject;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.validation.Error;

public class Address extends ValueObject {

    private String street;
    private String number;
    private String complement;
    private String city;
    private String state;
    private String zipCode;

    public Address(
            final String aStreet,
            final String aNumber,
            final String aComplement,
            final String aCity,
            final String aState,
            final String aZipCode
    ) {
        this.street = aStreet;
        this.number = aNumber;
        this.complement = aComplement;
        this.city = aCity;
        this.state = aState;
        this.zipCode = aZipCode;
        selfValidation();
    }

    public static Address newAddress(
            final String aStreet,
            final String aNumber,
            final String aComplement,
            final String aCity,
            final String aState,
            final String aZipCode
    ) {
        return new Address(
                aStreet,
                aNumber,
                aComplement,
                aCity,
                aState,
                aZipCode
        );
    }

    private void selfValidation() {
        if (this.street == null || this.street.isBlank()) {
            throw DomainException.with(new Error(CommonErrorMessage.nullOrBlank("street")));
        }

        if (this.number == null || this.number.isBlank()) {
            throw DomainException.with(new Error(CommonErrorMessage.nullOrBlank("number")));
        }

        if (this.city == null || this.city.isBlank()) {
            throw DomainException.with(new Error(CommonErrorMessage.nullOrBlank("city")));
        }

        if (this.state == null || this.state.isBlank()) {
            throw DomainException.with(new Error(CommonErrorMessage.nullOrBlank("state")));
        }

        if (this.zipCode == null || this.zipCode.isBlank()) {
            throw DomainException.with(new Error(CommonErrorMessage.nullOrBlank("zipCode")));
        }
    }

    public String getStreet() {
        return street;
    }

    public String getNumber() {
        return number;
    }

    public String getComplement() {
        return complement;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }
}
