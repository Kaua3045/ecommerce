package com.kaua.ecommerce.domain.customer;

import com.kaua.ecommerce.domain.AggregateRoot;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.ValidationHandler;

import java.time.Instant;

public class Address extends AggregateRoot<AddressID> {

    private String street;
    private String number;
    private String complement;
    private String zipCode;
    private Instant createdAt;
    private Instant updatedAt;

    private Address(
            final AddressID aAddressID,
            final String aStreet,
            final String aNumber,
            final String aComplement,
            final String aZipCode,
            final Instant aCreatedAt,
            final Instant aUpdatedAt
    ) {
        super(aAddressID);
        this.street = aStreet;
        this.number = aNumber;
        this.complement = aComplement;
        this.zipCode = aZipCode;
        this.createdAt = aCreatedAt;
        this.updatedAt = aUpdatedAt;
        selfValidation();
    }

    public static Address newAddress(
            final String aStreet,
            final String aNumber,
            final String aComplement,
            final String aZipCode
    ) {
        final var aId = AddressID.unique();
        final var aNow = InstantUtils.now();
        return new Address(
                aId,
                aStreet,
                aNumber,
                aComplement,
                aZipCode,
                aNow,
                aNow
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

    @Override
    public void validate(ValidationHandler handler) {

    }
}
