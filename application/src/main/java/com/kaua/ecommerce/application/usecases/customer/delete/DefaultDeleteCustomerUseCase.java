package com.kaua.ecommerce.application.usecases.customer.delete;

import com.kaua.ecommerce.application.gateways.CustomerGateway;

import java.util.Objects;

public class DefaultDeleteCustomerUseCase extends DeleteCustomerUseCase {

    private final CustomerGateway customerGateway;

    public DefaultDeleteCustomerUseCase(final CustomerGateway customerGateway) {
        this.customerGateway = Objects.requireNonNull(customerGateway);
    }

    @Override
    public void execute(String aAccountId) {
        this.customerGateway.deleteById(aAccountId);
    }
}
