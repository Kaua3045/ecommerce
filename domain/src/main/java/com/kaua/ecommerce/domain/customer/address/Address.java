package com.kaua.ecommerce.domain.customer.address;

import com.kaua.ecommerce.domain.AggregateRoot;
import com.kaua.ecommerce.domain.customer.CustomerID;
import com.kaua.ecommerce.domain.validation.ValidationHandler;

public class Address extends AggregateRoot<AddressID> {

    private String street;
    private String number;
    private String complement;
    private String district;
    private String city;
    private String state;
    private String zipCode;
    private CustomerID customerID;

    private Address(
            final AddressID aAddressID,
            final String aStreet,
            final String aNumber,
            final String aComplement,
            final String aDistrict,
            final String aCity,
            final String aState,
            final String aZipCode,
            final CustomerID aCustomerID
    ) {
        super(aAddressID);
        this.street = aStreet;
        this.number = aNumber;
        this.complement = aComplement;
        this.district = aDistrict;
        this.city = aCity;
        this.state = aState;
        this.zipCode = aZipCode;
        this.customerID = aCustomerID;
    }

    public static Address newAddress(
            final String aStreet,
            final String aNumber,
            final String aComplement,
            final String aDistrict,
            final String aCity,
            final String aState,
            final String aZipCode,
            final CustomerID aCustomerID
    ) {
        final var aId = AddressID.unique();
        return new Address(
                aId,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode,
                aCustomerID
        );
    }

    public static Address with(
            final String aAddressID,
            final String aStreet,
            final String aNumber,
            final String aComplement,
            final String aDistrict,
            final String aCity,
            final String aState,
            final String aZipCode,
            final String aCustomerID
    ) {
        return new Address(
                AddressID.from(aAddressID),
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode,
                CustomerID.from(aCustomerID)
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

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public CustomerID getCustomerID() {
        return customerID;
    }
}
