package com.kaua.ecommerce.application.usecases.customer.create;

import com.kaua.ecommerce.domain.customer.Customer;

public record CreateCustomerOutput(String id) {

    public static CreateCustomerOutput from(final Customer aCustomer) {
        return new CreateCustomerOutput(aCustomer.getId().getValue());
    }
}
