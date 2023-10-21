package com.kaua.ecommerce.application.usecases.customer.update.cpf;

import com.kaua.ecommerce.domain.customer.Customer;

public record UpdateCustomerCpfOutput(String accountId) {

    public static UpdateCustomerCpfOutput from(final Customer aCustomer) {
        return new UpdateCustomerCpfOutput(aCustomer.getAccountId());
    }
}
