package com.kaua.ecommerce.application.usecases.customer.update.cpf;

import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.gateways.CustomerGateway;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.utils.CpfValidator;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

import java.util.Objects;

public class DefaultUpdateCustomerCpfUseCase extends UpdateCustomerCpfUseCase {

    private final CustomerGateway customerGateway;

    public DefaultUpdateCustomerCpfUseCase(final CustomerGateway customerGateway) {
        this.customerGateway = Objects.requireNonNull(customerGateway);
    }

    @Override
    public Either<NotificationHandler, UpdateCustomerCpfOutput> execute(UpdateCustomerCpfCommand input) {
        final var aCustomer = this.customerGateway.findByAccountId(input.accountId())
                .orElseThrow(NotFoundException.with(Customer.class, input.accountId()));

        if (!CpfValidator.validateCpf(input.cpf())) {
            return Either.left(NotificationHandler.create(new Error("'cpf' invalid")));
        }

        final var aCustomerWithCpf = aCustomer.changeCpf(CpfValidator.cleanCpf(input.cpf()));
        this.customerGateway.update(aCustomerWithCpf);

        return Either.right(UpdateCustomerCpfOutput.from(aCustomerWithCpf));
    }
}
