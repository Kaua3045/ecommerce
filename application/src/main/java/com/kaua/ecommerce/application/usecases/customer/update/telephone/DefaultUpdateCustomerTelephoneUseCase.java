package com.kaua.ecommerce.application.usecases.customer.update.telephone;

import com.kaua.ecommerce.application.adapters.TelephoneAdapter;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.gateways.CustomerGateway;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.customer.Telephone;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

import java.util.Objects;

public class DefaultUpdateCustomerTelephoneUseCase extends UpdateCustomerTelephoneUseCase {

    private final CustomerGateway customerGateway;
    private final TelephoneAdapter telephoneAdapter;

    public DefaultUpdateCustomerTelephoneUseCase(
            final CustomerGateway customerGateway,
            final TelephoneAdapter telephoneAdapter
    ) {
        this.customerGateway = Objects.requireNonNull(customerGateway);
        this.telephoneAdapter = Objects.requireNonNull(telephoneAdapter);
    }

    @Override
    public Either<NotificationHandler, UpdateCustomerTelephoneOutput> execute(final UpdateCustomerTelephoneCommand input) {
        final var aCustomer = this.customerGateway.findByAccountId(input.accountId())
                .orElseThrow(NotFoundException.with(Customer.class, input.accountId()));

        final var aTelephoneValidation = this.telephoneAdapter.validate(input.telephone());

        if (!aTelephoneValidation) {
            return Either.left(NotificationHandler.create(new Error("'telephone' invalid")));
        }

        aCustomer.changeTelephone(Telephone.newTelephone(input.telephone()));

        this.customerGateway.update(aCustomer);

        return Either.right(UpdateCustomerTelephoneOutput.from(aCustomer));
    }
}
