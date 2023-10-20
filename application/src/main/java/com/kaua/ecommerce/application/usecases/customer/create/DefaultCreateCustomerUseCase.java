package com.kaua.ecommerce.application.usecases.customer.create;

import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.gateways.CustomerGateway;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

import java.util.Objects;

public class DefaultCreateCustomerUseCase extends CreateCustomerUseCase {

    private final CustomerGateway customerGateway;

    public DefaultCreateCustomerUseCase(final CustomerGateway customerGateway) {
        this.customerGateway = Objects.requireNonNull(customerGateway);
    }

    @Override
    public Either<NotificationHandler, CreateCustomerOutput> execute(CreateCustomerCommand input) {
        final var aNotification = NotificationHandler.create();

        if (this.customerGateway.existsByAccountId(input.accountId())) {
            return Either.left(aNotification.append(new Error("Customer already exists with this account id")));
        }

        final var aCustomer = Customer.newCustomer(
                input.accountId(),
                input.firstName(),
                input.lastName(),
                input.email()
        );
        aCustomer.validate(aNotification);

        return aNotification.hasError()
                ? Either.left(aNotification)
                : Either.right(CreateCustomerOutput.from(this.customerGateway.create(aCustomer)));
    }
}
