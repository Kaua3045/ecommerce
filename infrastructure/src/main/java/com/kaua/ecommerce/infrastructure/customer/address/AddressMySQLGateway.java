package com.kaua.ecommerce.infrastructure.customer.address;

import com.kaua.ecommerce.application.gateways.AddressDatabaseGateway;
import com.kaua.ecommerce.infrastructure.customer.address.persistence.AddressJpaEntityRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AddressMySQLGateway implements AddressDatabaseGateway {

    private final AddressJpaEntityRepository addressEntityRepository;

    public AddressMySQLGateway(final AddressJpaEntityRepository addressEntityRepository) {
        this.addressEntityRepository = Objects.requireNonNull(addressEntityRepository);
    }

    @Override
    public void deleteById(String aId) {
        if (this.addressEntityRepository.existsById(aId)) {
            this.addressEntityRepository.deleteById(aId);
        }
    }
}
