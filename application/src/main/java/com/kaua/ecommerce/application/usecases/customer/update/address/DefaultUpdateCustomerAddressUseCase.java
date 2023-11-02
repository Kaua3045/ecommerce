package com.kaua.ecommerce.application.usecases.customer.update.address;

import com.kaua.ecommerce.application.adapters.AddressAdapter;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.gateways.AddressGateway;
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
    private final AddressAdapter addressAdapter;

    public DefaultUpdateCustomerAddressUseCase(
            final CustomerGateway customerGateway,
            final AddressGateway addressGateway,
            final AddressAdapter addressAdapter
    ) {
        this.customerGateway = Objects.requireNonNull(customerGateway);
        this.addressGateway = Objects.requireNonNull(addressGateway);
        this.addressAdapter = Objects.requireNonNull(addressAdapter);
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
                input.zipCode()
        );
        aAddress.validate(aNotification);

        if (aNotification.hasError()) {
            return Either.left(aNotification);
        }

        final var aOldAddressId = aCustomer.getAddress() == null
                ? null
                : aCustomer.getAddress().getId().getValue();

        final var aCustomerWithAddress = aCustomer.changeAddress(aAddress);
        this.customerGateway.update(aCustomerWithAddress);

        if (aOldAddressId != null) {
            this.addressGateway.deleteById(aOldAddressId);
        }

        return Either.right(UpdateCustomerAddressOutput.from(aCustomerWithAddress));
    }

    private NotificationHandler checkAddress(final UpdateCustomerAddressCommand input) {
        final var aNotification = NotificationHandler.create();

        final var aAddressResponse = this.addressAdapter.findAddressByZipCode(input.zipCode());

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
