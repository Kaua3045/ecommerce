package com.kaua.ecommerce.application.usecases.customer.update.telephone;

import com.kaua.ecommerce.domain.customer.Customer;

public record UpdateCustomerTelephoneOutput(String accountId) {

    public static UpdateCustomerTelephoneOutput from(final Customer aCustomer) {
        return new UpdateCustomerTelephoneOutput(aCustomer.getAccountId());
    }
}
