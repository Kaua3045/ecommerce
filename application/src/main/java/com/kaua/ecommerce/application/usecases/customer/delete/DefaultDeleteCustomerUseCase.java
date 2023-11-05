package com.kaua.ecommerce.application.usecases.customer.delete;

import com.kaua.ecommerce.application.gateways.CacheGateway;
import com.kaua.ecommerce.application.gateways.CustomerGateway;
import com.kaua.ecommerce.domain.customer.Customer;

import java.util.Objects;

public class DefaultDeleteCustomerUseCase extends DeleteCustomerUseCase {

    private final CustomerGateway customerGateway;
    private final CacheGateway<Customer> customerCacheGateway;

    public DefaultDeleteCustomerUseCase(
            final CustomerGateway customerGateway,
            final CacheGateway<Customer> customerCacheGateway
    ) {
        this.customerGateway = Objects.requireNonNull(customerGateway);
        this.customerCacheGateway = Objects.requireNonNull(customerCacheGateway);
    }

    @Override
    public void execute(String aAccountId) {
        this.customerGateway.deleteById(aAccountId);
        this.customerCacheGateway.delete(aAccountId);
    }
}
