package com.kaua.ecommerce.application.usecases.customer.update.telephone;

public record UpdateCustomerTelephoneCommand(String accountId, String telephone) {

    public static UpdateCustomerTelephoneCommand with(final String accountId, final String telephone) {
        return new UpdateCustomerTelephoneCommand(accountId, telephone);
    }
}
