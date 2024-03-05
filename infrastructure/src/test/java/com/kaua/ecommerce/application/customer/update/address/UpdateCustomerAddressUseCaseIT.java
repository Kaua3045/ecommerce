package com.kaua.ecommerce.application.customer.update.address;

import com.kaua.ecommerce.application.gateways.responses.AddressResponse;
import com.kaua.ecommerce.application.usecases.customer.update.address.UpdateCustomerAddressCommand;
import com.kaua.ecommerce.application.usecases.customer.update.address.UpdateCustomerAddressUseCase;
import com.kaua.ecommerce.config.CacheTestConfiguration;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.infrastructure.CacheGatewayTest;
import com.kaua.ecommerce.infrastructure.api.ViaCepClient;
import com.kaua.ecommerce.infrastructure.customer.address.persistence.AddressJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerCacheEntityRepository;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaEntity;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@CacheGatewayTest
public class UpdateCustomerAddressUseCaseIT extends CacheTestConfiguration {

    @Autowired
    private UpdateCustomerAddressUseCase updateCustomerAddressUseCase;

    @Autowired
    private CustomerJpaEntityRepository customerRepository;

    @Autowired
    private CustomerCacheEntityRepository customerCacheRepository;

    @Autowired
    private AddressJpaEntityRepository addressRepository;

    @MockBean
    private ViaCepClient viaCepClient;

    @Test
    void givenAValidValues_whenCallsUpdateCustomerAddress_shouldReturnAccountId() {
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aAccountId = aCustomer.getAccountId();
        final var aStreet = "Rua Vitorino Carmilo";
        final var aNumber = "123";
        final var aComplement = "Casa";
        final var aDistrict = "Barra Funda";
        final var aCity = "São Paulo";
        final var aState = "SP";
        final var aZipCode = "01153000";

        this.customerRepository.save(CustomerJpaEntity.toEntity(aCustomer));

        final var aCommand = UpdateCustomerAddressCommand
                .with(aAccountId, aStreet, aNumber, aComplement, aDistrict, aCity, aState, aZipCode);

        Mockito.when(this.viaCepClient.getAddressByZipCode(aZipCode))
                .thenReturn(new AddressResponse(aZipCode, aStreet, aComplement, aDistrict, aCity, aState));

        final var aResult = this.updateCustomerAddressUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aResult);
        Assertions.assertNotNull(aResult.accountId());

        final var aCustomerUpdated = this.customerRepository.findByAccountId(aAccountId).get();

        Assertions.assertEquals(aCustomer.getId().getValue(), aCustomerUpdated.getId());
        Assertions.assertEquals(aAccountId, aCustomerUpdated.getAccountId());
        Assertions.assertEquals(aCustomer.getFirstName(), aCustomerUpdated.getFirstName());
        Assertions.assertEquals(aCustomer.getLastName(), aCustomerUpdated.getLastName());
        Assertions.assertEquals(aCustomer.getEmail(), aCustomerUpdated.getEmail());
        Assertions.assertNull(aCustomerUpdated.getCpf());
        Assertions.assertNull(aCustomerUpdated.getTelephone());
        Assertions.assertEquals(aStreet, aCustomerUpdated.getAddress().get().getStreet());
        Assertions.assertEquals(aNumber, aCustomerUpdated.getAddress().get().getNumber());
        Assertions.assertEquals(aComplement, aCustomerUpdated.getAddress().get().getComplement());
        Assertions.assertEquals(aDistrict, aCustomerUpdated.getAddress().get().getDistrict());
        Assertions.assertEquals(aCity, aCustomerUpdated.getAddress().get().getCity());
        Assertions.assertEquals(aState, aCustomerUpdated.getAddress().get().getState());
        Assertions.assertEquals(aZipCode, aCustomerUpdated.getAddress().get().getZipCode());
        Assertions.assertEquals(aCustomer.getId().getValue(), aCustomerUpdated.getAddress().get().getCustomerId());
        Assertions.assertEquals(aCustomer.getCreatedAt(), aCustomerUpdated.getCreatedAt());
        Assertions.assertTrue(aCustomer.getUpdatedAt().isBefore(aCustomerUpdated.getUpdatedAt()));
        Assertions.assertEquals(1, this.customerRepository.count());
        Assertions.assertEquals(1, this.addressRepository.count());
        Assertions.assertEquals(0, this.customerCacheRepository.count());
    }

    @Test
    void givenAnInvalidAccountId_whenCallsUpdateCustomerAddress_shouldThrowNotFoundException() {
        final var aAccountId = IdUtils.generate();
        final var aStreet = "Rua Vitorino Carmilo";
        final var aNumber = "123";
        final var aComplement = "Casa";
        final var aDistrict = "Barra Funda";
        final var aCity = "São Paulo";
        final var aState = "SP";
        final var aZipCode = "01153000";

        final var expectedErrorMessage = Fixture.notFoundMessage(Customer.class, aAccountId);

        final var aCommand = UpdateCustomerAddressCommand
                .with(aAccountId, aStreet, aNumber, aComplement, aDistrict, aCity, aState, aZipCode);

        final var aResult = Assertions.assertThrows(NotFoundException.class,
                () -> this.updateCustomerAddressUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aResult.getMessage());
        Assertions.assertEquals(0, this.customerRepository.count());
        Assertions.assertEquals(0, this.customerCacheRepository.count());
        Assertions.assertEquals(0, this.addressRepository.count());
    }

    @Test
    void givenAnInvalidAddressZipCode_whenCallsUpdateCustomerAddress_shouldReturnDomainException() {
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aAccountId = aCustomer.getAccountId();
        final var aStreet = "Rua Vitorino Carmilo";
        final var aNumber = "123";
        final var aComplement = "Casa";
        final var aDistrict = "Barra Funda";
        final var aCity = "São Paulo";
        final var aState = "SP";
        final var aZipCode = "01153001";

        this.customerRepository.save(CustomerJpaEntity.toEntity(aCustomer));

        final var expectedErrorMessage = "'zipCode' not exists";
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCustomerAddressCommand
                .with(aAccountId, aStreet, aNumber, aComplement, aDistrict, aCity, aState, aZipCode);

        Mockito.when(this.viaCepClient.getAddressByZipCode(aZipCode))
                .thenReturn(new AddressResponse(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                ));

        final var aResult = this.updateCustomerAddressUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(1, this.customerRepository.count());
        Assertions.assertEquals(0, this.customerCacheRepository.count());
        Assertions.assertEquals(0, this.addressRepository.count());
    }
}
