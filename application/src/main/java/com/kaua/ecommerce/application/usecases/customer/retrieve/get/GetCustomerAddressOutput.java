package com.kaua.ecommerce.application.usecases.customer.retrieve.get;

import com.kaua.ecommerce.domain.customer.address.Address;

public record GetCustomerAddressOutput(
        String street,
        String number,
        String complement,
        String district,
        String city,
        String state,
        String zipCode
) {

    public static GetCustomerAddressOutput from(final Address aAddress) {
        return new GetCustomerAddressOutput(
                aAddress.getStreet(),
                aAddress.getNumber(),
                aAddress.getComplement(),
                aAddress.getDistrict(),
                aAddress.getCity(),
                aAddress.getState(),
                aAddress.getZipCode()
        );
    }
}
