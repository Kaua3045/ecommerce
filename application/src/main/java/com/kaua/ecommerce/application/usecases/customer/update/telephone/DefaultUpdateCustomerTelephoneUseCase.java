package com.kaua.ecommerce.application.usecases.customer.update.telephone;

import com.kaua.ecommerce.application.adapters.TelephoneAdapter;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.gateways.CacheGateway;
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
    private final CacheGateway<Customer> customerCacheGateway;

    public DefaultUpdateCustomerTelephoneUseCase(
            final CustomerGateway customerGateway,
            final TelephoneAdapter telephoneAdapter,
            final CacheGateway<Customer> customerCacheGateway
    ) {
        this.customerGateway = Objects.requireNonNull(customerGateway);
        this.telephoneAdapter = Objects.requireNonNull(telephoneAdapter);
        this.customerCacheGateway = Objects.requireNonNull(customerCacheGateway);
    }

    @Override
    public Either<NotificationHandler, UpdateCustomerTelephoneOutput> execute(final UpdateCustomerTelephoneCommand input) {
        final var aCustomer = this.customerGateway.findByAccountId(input.accountId())
                .orElseThrow(NotFoundException.with(Customer.class, input.accountId()));
        final var aFormattedTelephone = this.telephoneAdapter.formatInternational(input.telephone());

        final var aTelephoneValidation = this.telephoneAdapter.validate(aFormattedTelephone);

        if (!aTelephoneValidation) {
            return Either.left(NotificationHandler.create(new Error("'telephone' invalid")));
        }

        aCustomer.changeTelephone(Telephone.newTelephone(aFormattedTelephone));

        this.customerGateway.update(aCustomer);
        this.customerCacheGateway.delete(aCustomer.getAccountId());

        return Either.right(UpdateCustomerTelephoneOutput.from(aCustomer));
    }
}
