package com.kaua.ecommerce.application.usecases.customer.retrieve.get;

import com.kaua.ecommerce.application.gateways.CustomerGateway;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;

import java.util.Objects;

public class DefaultGetCustomerByAccountIdUseCase extends GetCustomerByAccountIdUseCase {

    private final CustomerGateway customerGateway;

    public DefaultGetCustomerByAccountIdUseCase(final CustomerGateway customerGateway) {
        this.customerGateway = Objects.requireNonNull(customerGateway);
    }

    @Override
    public GetCustomerByAccountIdOutput execute(String aAccountId) {
        return this.customerGateway.findByAccountId(aAccountId)
                .map(GetCustomerByAccountIdOutput::from)
                .orElseThrow(NotFoundException.with(Customer.class, aAccountId));
    }
}
