package com.kaua.ecommerce.application.usecases.customer.update.cpf;

import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.gateways.CacheGateway;
import com.kaua.ecommerce.application.gateways.CustomerGateway;
import com.kaua.ecommerce.domain.customer.Cpf;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

import java.util.Objects;

public class DefaultUpdateCustomerCpfUseCase extends UpdateCustomerCpfUseCase {

    private final CustomerGateway customerGateway;
    private final CacheGateway<Customer> customerCacheGateway;

    public DefaultUpdateCustomerCpfUseCase(
            final CustomerGateway customerGateway,
            final CacheGateway<Customer> customerCacheGateway
    ) {
        this.customerGateway = Objects.requireNonNull(customerGateway);
        this.customerCacheGateway = Objects.requireNonNull(customerCacheGateway);
    }

    @Override
    public Either<NotificationHandler, UpdateCustomerCpfOutput> execute(UpdateCustomerCpfCommand input) {
        final var aCustomer = this.customerGateway.findByAccountId(input.accountId())
                .orElseThrow(NotFoundException.with(Customer.class, input.accountId()));

        final var aCustomerWithCpf = aCustomer.changeCpf(Cpf.newCpf(input.cpf()));
        this.customerGateway.update(aCustomerWithCpf);
        this.customerCacheGateway.delete(aCustomerWithCpf.getAccountId());

        return Either.right(UpdateCustomerCpfOutput.from(aCustomerWithCpf));
    }
}
