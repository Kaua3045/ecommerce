package com.kaua.ecommerce.application.usecases.customer.retrieve.get;

import com.kaua.ecommerce.application.gateways.CacheGateway;
import com.kaua.ecommerce.application.gateways.CustomerGateway;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;

import java.util.Objects;

public class DefaultGetCustomerByAccountIdUseCase extends GetCustomerByAccountIdUseCase {

    private final CustomerGateway customerGateway;
    private final CacheGateway<Customer> customerCacheGateway;

    public DefaultGetCustomerByAccountIdUseCase(
            final CustomerGateway customerGateway,
            final CacheGateway<Customer> customerCacheGateway
    ) {
        this.customerGateway = Objects.requireNonNull(customerGateway);
        this.customerCacheGateway = Objects.requireNonNull(customerCacheGateway);
    }

    @Override
    public GetCustomerByAccountIdOutput execute(String aAccountId) {
        return this.customerCacheGateway.get(aAccountId)
                .map(GetCustomerByAccountIdOutput::from)
                .orElseGet(() -> this.customerGateway.findByAccountId(aAccountId)
                        .map(customer -> {
                            this.customerCacheGateway.save(customer);
                            return GetCustomerByAccountIdOutput.from(customer);
                        }).orElseThrow(NotFoundException.with(Customer.class, aAccountId)));
    }
}
