package com.kaua.ecommerce.infrastructure.customer.address;

import com.kaua.ecommerce.application.gateways.AddressGateway;
import com.kaua.ecommerce.infrastructure.customer.address.persistence.AddressJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AddressMySQLGateway implements AddressGateway {

    private final AddressJpaRepository addressRepository;

    public AddressMySQLGateway(final AddressJpaRepository addressRepository) {
        this.addressRepository = Objects.requireNonNull(addressRepository);
    }

    @Override
    public void deleteById(String aId) {
        if (this.addressRepository.existsById(aId)) {
            this.addressRepository.deleteById(aId);
        }
    }
}
