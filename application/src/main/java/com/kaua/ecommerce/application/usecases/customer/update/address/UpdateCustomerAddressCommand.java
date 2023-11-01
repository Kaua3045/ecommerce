package com.kaua.ecommerce.application.usecases.customer.update.address;

public record UpdateCustomerAddressCommand(
        String accountId,
        String street,
        String number,
        String complement,
        String district,
        String zipCode
) {

    public static UpdateCustomerAddressCommand with(
            final String aAccountId,
            final String aStreet,
            final String aNumber,
            final String aComplement,
            final String aDistrict,
            final String aZipCode
    ) {
        return new UpdateCustomerAddressCommand(
                aAccountId,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aZipCode
        );
    }
}
