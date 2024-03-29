package com.kaua.ecommerce.application.usecases.customer.update.address;

import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.gateways.AddressDatabaseGateway;
import com.kaua.ecommerce.application.gateways.AddressGateway;
import com.kaua.ecommerce.application.gateways.CacheGateway;
import com.kaua.ecommerce.application.gateways.CustomerGateway;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.customer.address.Address;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

import java.util.Objects;

public class DefaultUpdateCustomerAddressUseCase extends UpdateCustomerAddressUseCase {

    private final CustomerGateway customerGateway;
    private final AddressGateway addressGateway;
    private final AddressDatabaseGateway addressDatabaseGateway;
    private final CacheGateway<Customer> customerCacheGateway;

    public DefaultUpdateCustomerAddressUseCase(
            final CustomerGateway customerGateway,
            final AddressGateway addressGateway,
            final AddressDatabaseGateway addressDatabaseGateway,
            final CacheGateway<Customer> customerCacheGateway
    ) {
        this.customerGateway = Objects.requireNonNull(customerGateway);
        this.addressGateway = Objects.requireNonNull(addressGateway);
        this.addressDatabaseGateway = Objects.requireNonNull(addressDatabaseGateway);
        this.customerCacheGateway = Objects.requireNonNull(customerCacheGateway);
    }

    @Override
    public Either<NotificationHandler, UpdateCustomerAddressOutput> execute(final UpdateCustomerAddressCommand input) {
        final var aNotification = NotificationHandler.create();

        final var aCustomer = this.customerGateway.findByAccountId(input.accountId())
                .orElseThrow(NotFoundException.with(Customer.class, input.accountId()));

        final var aCheckedAddressData = checkAddress(input);
        if (aCheckedAddressData.hasError()) {
            return Either.left(aCheckedAddressData);
        }

        final var aAddress = Address.newAddress(
                input.street(),
                input.number(),
                input.complement(),
                input.district(),
                input.city(),
                input.state(),
                input.zipCode(),
                aCustomer.getId()
        );
        aAddress.validate(aNotification);

        if (aNotification.hasError()) {
            return Either.left(aNotification);
        }

        final var aOldAddressId = aCustomer.getAddress().map(Address::getId).orElse(null);

        final var aCustomerWithAddress = aCustomer.changeAddress(aAddress);
        this.customerGateway.update(aCustomerWithAddress);

        if (aOldAddressId != null) {
            this.addressDatabaseGateway.deleteById(aOldAddressId.getValue());
        }

        this.customerCacheGateway.delete(aCustomerWithAddress.getAccountId());

        return Either.right(UpdateCustomerAddressOutput.from(aCustomerWithAddress));
    }

    private NotificationHandler checkAddress(final UpdateCustomerAddressCommand input) {
        final var aNotification = NotificationHandler.create();

        final var aAddressResponse = this.addressGateway.findAddressByZipCodeInExternalService(input.zipCode());

        if (aAddressResponse.isEmpty()) {
            return aNotification.append(new Error("'zipCode' not exists"));
        }

        if (!aAddressResponse.get().localidade().equalsIgnoreCase(input.city())) {
            aNotification.append(new Error("'city' not exists"));
        }

        if (!aAddressResponse.get().uf().equalsIgnoreCase(input.state())) {
            aNotification.append(new Error("'state' not exists"));
        }

        return aNotification;
    }
}
