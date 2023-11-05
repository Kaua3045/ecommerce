package com.kaua.ecommerce.infrastructure.costumer.address;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.customer.address.AddressMySQLGateway;
import com.kaua.ecommerce.infrastructure.customer.address.persistence.AddressJpaEntity;
import com.kaua.ecommerce.infrastructure.customer.address.persistence.AddressJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class AddressGatewayTest {

    @Autowired
    private AddressMySQLGateway addressGateway;

    @Autowired
    private AddressJpaRepository addressRepository;

    @Test
    void givenAValidAddressId_whenCallDeleteById_shouldBeOk() {
        final var aAddress = Fixture.Addresses.addressDefault;

        Assertions.assertEquals(0, addressRepository.count());
        addressRepository.save(AddressJpaEntity.toEntity(aAddress));
        Assertions.assertEquals(1, addressRepository.count());

        addressGateway.deleteById(aAddress.getId().getValue());

        Assertions.assertEquals(0, addressRepository.count());
    }

    @Test
    void givenAnInvalidAddressId_whenCallDeleteById_shouldBeOk() {
        final var aAddressId = "123";

        Assertions.assertEquals(0, addressRepository.count());

        addressGateway.deleteById(aAddressId);

        Assertions.assertEquals(0, addressRepository.count());
    }
}
