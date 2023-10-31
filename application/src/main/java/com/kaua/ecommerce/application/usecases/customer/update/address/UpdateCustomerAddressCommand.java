package com.kaua.ecommerce.application.usecases.customer.update.address;

public record UpdateCustomerAddressCommand(
        String accountId,
        String street,
        String number,
        String complement,
        String city,
        String state,
        String zipCode
) {

    public static UpdateCustomerAddressCommand with(
            final String aAccountId,
            final String aStreet,
            final String aNumber,
            final String aComplement,
            final String aCity,
            final String aState,
            final String aZipCode
    ) {
        return new UpdateCustomerAddressCommand(
                aAccountId,
                aStreet,
                aNumber,
                aComplement,
                aCity,
                aState,
                aZipCode
        );
    }
}
