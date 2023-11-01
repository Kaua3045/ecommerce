package com.kaua.ecommerce.domain.customer.address;

import com.kaua.ecommerce.domain.AggregateRoot;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.validation.ValidationHandler;

import java.time.Instant;

public class Address extends AggregateRoot<AddressID> {

    private String street;
    private String number;
    private String complement;
    private String district;
    private String zipCode;
    private Instant createdAt;
    private Instant updatedAt;

    private Address(
            final AddressID aAddressID,
            final String aStreet,
            final String aNumber,
            final String aComplement,
            final String aDistrict,
            final String aZipCode,
            final Instant aCreatedAt,
            final Instant aUpdatedAt
    ) {
        super(aAddressID);
        this.street = aStreet;
        this.number = aNumber;
        this.complement = aComplement;
        this.district = aDistrict;
        this.zipCode = aZipCode;
        this.createdAt = aCreatedAt;
        this.updatedAt = aUpdatedAt;
    }

    public static Address newAddress(
            final String aStreet,
            final String aNumber,
            final String aComplement,
            final String aDistrict,
            final String aZipCode
    ) {
        final var aId = AddressID.unique();
        final var aNow = InstantUtils.now();
        return new Address(
                aId,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aZipCode,
                aNow,
                aNow
        );
    }

    public static Address with(
            final String aAddressID,
            final String aStreet,
            final String aNumber,
            final String aComplement,
            final String aDistrict,
            final String aZipCode,
            final Instant aCreatedAt,
            final Instant aUpdatedAt
    ) {
        return new Address(
                AddressID.from(aAddressID),
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aZipCode,
                aCreatedAt,
                aUpdatedAt
        );
    }

    @Override
    public void validate(ValidationHandler handler) {
        new AddressValidation(this, handler).validate();
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

    public String getDistrict() {
        return district;
    }

    public String getZipCode() {
        return zipCode;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
