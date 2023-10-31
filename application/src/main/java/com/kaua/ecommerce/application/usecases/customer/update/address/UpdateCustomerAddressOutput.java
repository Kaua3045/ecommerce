package com.kaua.ecommerce.application.usecases.customer.update.address;

import com.kaua.ecommerce.domain.customer.Customer;

public record UpdateCustomerAddressOutput(String accountId) {

    public static UpdateCustomerAddressOutput from(final Customer aCustomer) {
        return new UpdateCustomerAddressOutput(aCustomer.getAccountId());
    }
}
