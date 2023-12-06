package com.kaua.ecommerce.application.usecases.customer.update.address;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.adapters.AddressAdapter;
import com.kaua.ecommerce.application.adapters.responses.AddressResponse;
import com.kaua.ecommerce.application.gateways.AddressGateway;
import com.kaua.ecommerce.application.gateways.CacheGateway;
import com.kaua.ecommerce.application.gateways.CustomerGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Objects;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

public class UpdateCustomerAddressUseCaseTest extends UseCaseTest {

    @Mock
    private CustomerGateway customerGateway;

    @Mock
    private AddressAdapter addressAdapter;

    @Mock
    private AddressGateway addressGateway;

    @Mock
    private CacheGateway<Customer> customerCacheGateway;

    @InjectMocks
    private DefaultUpdateCustomerAddressUseCase useCase;

    @Test
    void givenAValidCommand_whenCallChangeAddress_shouldReturnAnAccountId() {
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aAccountId = aCustomer.getAccountId();

        final var aStreet = "Rua Teste";
        final var aNumber = "123";
        final var aComplement = "Complemento Teste";
        final var aDistrict = "Bairro Teste";
        final var aCity = "Cidade Teste";
        final var aState = "Estado Teste";
        final var aZipCode = "12345678";

        final var aCommand = UpdateCustomerAddressCommand.with(
                aAccountId,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode);

        Mockito.when(customerGateway.findByAccountId(Mockito.any())).thenReturn(Optional.of(aCustomer));
        Mockito.when(addressAdapter.findAddressByZipCode(aZipCode))
                .thenReturn(Optional.of(makeAddressResponse()));
        Mockito.when(customerGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());
        Mockito.doNothing().when(customerCacheGateway).delete(aAccountId);

        final var aResult = useCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aResult);
        Assertions.assertEquals(aAccountId, aResult.accountId());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aAccountId);
        Mockito.verify(addressAdapter, Mockito.times(1)).findAddressByZipCode(aZipCode);
        Mockito.verify(addressGateway, Mockito.times(0)).deleteById(Mockito.any());
        Mockito.verify(customerGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aAccountId, aCmd.getAccountId())
                && Objects.equals(aCustomer.getId(), aCmd.getId())
                && Objects.equals(aCustomer.getFirstName(), aCmd.getFirstName())
                && Objects.equals(aCustomer.getLastName(), aCmd.getLastName())
                && Objects.equals(aCustomer.getEmail(), aCmd.getEmail())
                && aCmd.getCpf().isEmpty()
                && aCmd.getTelephone().isEmpty()
                && Objects.equals(aStreet, aCmd.getAddress().get().getStreet())
                && Objects.equals(aNumber, aCmd.getAddress().get().getNumber())
                && Objects.equals(aComplement, aCmd.getAddress().get().getComplement())
                && Objects.equals(aDistrict, aCmd.getAddress().get().getDistrict())
                && Objects.equals(aCity, aCmd.getAddress().get().getCity())
                && Objects.equals(aState, aCmd.getAddress().get().getState())
                && Objects.equals(aZipCode, aCmd.getAddress().get().getZipCode())
                && Objects.equals(aCustomer.getCreatedAt(), aCmd.getCreatedAt())
                && Objects.equals(aCustomer.getUpdatedAt(), aCmd.getUpdatedAt())));
        Mockito.verify(customerCacheGateway, Mockito.times(1)).delete(aAccountId);
    }

    @Test
    void givenAValidCommandAndCustomerWithAddress_whenCallChangeAddress_shouldReturnAnAccountId() {
        final var aCustomer = Fixture.Customers.customerWithAllParams;
        final var aAccountId = aCustomer.getAccountId();

        final var aStreet = "Rua Teste";
        final var aNumber = "123";
        final var aComplement = "Complemento Teste";
        final var aDistrict = "Bairro Teste";
        final var aCity = "Cidade Teste";
        final var aState = "Estado Teste";
        final var aZipCode = "12345678";

        final var aCommand = UpdateCustomerAddressCommand.with(
                aAccountId,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode);

        Mockito.when(customerGateway.findByAccountId(aAccountId)).thenReturn(Optional.of(aCustomer));
        Mockito.when(addressAdapter.findAddressByZipCode(aZipCode))
                .thenReturn(Optional.of(makeAddressResponse()));
        Mockito.doNothing().when(addressGateway).deleteById(Mockito.any());
        Mockito.when(customerGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());
        Mockito.doNothing().when(customerCacheGateway).delete(aAccountId);

        final var aResult = useCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aResult);
        Assertions.assertEquals(aAccountId, aResult.accountId());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aAccountId);
        Mockito.verify(addressAdapter, Mockito.times(1)).findAddressByZipCode(aZipCode);
        Mockito.verify(addressGateway, Mockito.times(1)).deleteById(Mockito.any());
        Mockito.verify(customerGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aAccountId, aCmd.getAccountId())
                        && Objects.equals(aCustomer.getId(), aCmd.getId())
                        && Objects.equals(aCustomer.getFirstName(), aCmd.getFirstName())
                        && Objects.equals(aCustomer.getLastName(), aCmd.getLastName())
                        && Objects.equals(aCustomer.getEmail(), aCmd.getEmail())
                        && Objects.equals(aCustomer.getCpf().get().getValue(), aCmd.getCpf().get().getValue())
                        && Objects.equals(aCustomer.getTelephone().get().getValue(), aCmd.getTelephone().get().getValue())
                        && Objects.equals(aStreet, aCmd.getAddress().get().getStreet())
                        && Objects.equals(aNumber, aCmd.getAddress().get().getNumber())
                        && Objects.equals(aComplement, aCmd.getAddress().get().getComplement())
                        && Objects.equals(aDistrict, aCmd.getAddress().get().getDistrict())
                        && Objects.equals(aCity, aCmd.getAddress().get().getCity())
                        && Objects.equals(aState, aCmd.getAddress().get().getState())
                        && Objects.equals(aZipCode, aCmd.getAddress().get().getZipCode())
                        && Objects.equals(aCustomer.getCreatedAt(), aCmd.getCreatedAt())
                        && Objects.equals(aCustomer.getUpdatedAt(), aCmd.getUpdatedAt())));
        Mockito.verify(customerCacheGateway, Mockito.times(1)).delete(aAccountId);
    }

    @Test
    void givenAnInvalidAccountId_whenCallChangeAddress_shouldThrowNotFoundException() {
        final var aAccountId = "123456789";

        final var aStreet = "Rua Teste";
        final var aNumber = "123";
        final var aComplement = "Complemento Teste";
        final var aDistrict = "Bairro Teste";
        final var aCity = "Cidade Teste";
        final var aState = "Estado Teste";
        final var aZipCode = "12345678";

        final var expectedErrorMessage = Fixture.notFoundMessage(Customer.class, aAccountId);

        final var aCommand = UpdateCustomerAddressCommand.with(
                aAccountId,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode);

        Mockito.when(customerGateway.findByAccountId(aAccountId)).thenReturn(Optional.empty());

        final var aResult = Assertions.assertThrows(NotFoundException.class,
                () -> useCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aResult.getMessage());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aAccountId);
        Mockito.verify(addressAdapter, Mockito.times(0)).findAddressByZipCode(aZipCode);
        Mockito.verify(addressGateway, Mockito.times(0)).deleteById(Mockito.any());
        Mockito.verify(customerGateway, Mockito.times(0)).update(Mockito.any());
        Mockito.verify(customerCacheGateway, Mockito.times(0)).delete(aAccountId);
    }

    @Test
    void givenAnInvalidZipCode_whenCallChangeAddress_shouldReturnDomainException() {
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aAccountId = aCustomer.getAccountId();

        final var aStreet = "Rua Teste";
        final var aNumber = "123";
        final var aComplement = "Complemento Teste";
        final var aDistrict = "Bairro Teste";
        final var aCity = "Cidade Teste";
        final var aState = "Estado Teste";
        final var aZipCode = "12345678";

        final var expectedErrorMessage = "'zipCode' not exists";
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCustomerAddressCommand.with(
                aAccountId,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode);

        Mockito.when(customerGateway.findByAccountId(aAccountId)).thenReturn(Optional.of(aCustomer));
        Mockito.when(addressAdapter.findAddressByZipCode(aZipCode)).thenReturn(Optional.empty());

        final var aResult = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aAccountId);
        Mockito.verify(addressAdapter, Mockito.times(1)).findAddressByZipCode(aZipCode);
        Mockito.verify(addressGateway, Mockito.times(0)).deleteById(Mockito.any());
        Mockito.verify(customerGateway, Mockito.times(0)).update(Mockito.any());
        Mockito.verify(customerCacheGateway, Mockito.times(0)).delete(aAccountId);
    }

    @Test
    void givenAnInvalidNullStreet_whenCallChangeAddress_shouldReturnDomainException() {
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aAccountId = aCustomer.getAccountId();

        final String aStreet = null;
        final var aNumber = "123";
        final var aComplement = "Complemento Teste";
        final var aDistrict = "Bairro Teste";
        final var aCity = "Cidade Teste";
        final var aState = "Estado Teste";
        final var aZipCode = "12345678";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("street");
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCustomerAddressCommand.with(
                aAccountId,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode);

        Mockito.when(customerGateway.findByAccountId(aAccountId)).thenReturn(Optional.of(aCustomer));
        Mockito.when(addressAdapter.findAddressByZipCode(aZipCode))
                .thenReturn(Optional.of(makeAddressResponse()));

        final var aResult = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aAccountId);
        Mockito.verify(addressAdapter, Mockito.times(1)).findAddressByZipCode(aZipCode);
        Mockito.verify(addressGateway, Mockito.times(0)).deleteById(Mockito.any());
        Mockito.verify(customerGateway, Mockito.times(0)).update(Mockito.any());
        Mockito.verify(customerCacheGateway, Mockito.times(0)).delete(aAccountId);
    }

    @Test
    void givenAnInvalidNullNumber_whenCallChangeAddress_shouldReturnDomainException() {
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aAccountId = aCustomer.getAccountId();

        final var aStreet = "Rua Teste";
        final String aNumber = null;
        final var aComplement = "Complemento Teste";
        final var aDistrict = "Bairro Teste";
        final var aCity = "Cidade Teste";
        final var aState = "Estado Teste";
        final var aZipCode = "12345678";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("number");
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCustomerAddressCommand.with(
                aAccountId,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode);

        Mockito.when(customerGateway.findByAccountId(aAccountId)).thenReturn(Optional.of(aCustomer));
        Mockito.when(addressAdapter.findAddressByZipCode(aZipCode))
                .thenReturn(Optional.of(makeAddressResponse()));

        final var aResult = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aAccountId);
        Mockito.verify(addressAdapter, Mockito.times(1)).findAddressByZipCode(aZipCode);
        Mockito.verify(addressGateway, Mockito.times(0)).deleteById(Mockito.any());
        Mockito.verify(customerGateway, Mockito.times(0)).update(Mockito.any());
        Mockito.verify(customerCacheGateway, Mockito.times(0)).delete(aAccountId);
    }

    @Test
    void givenAnInvalidNullDistrict_whenCallChangeAddress_shouldReturnDomainException() {
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aAccountId = aCustomer.getAccountId();

        final var aStreet = "Rua Teste";
        final var aNumber = "123";
        final var aComplement = "Complemento Teste";
        final String aDistrict = null;
        final var aCity = "Cidade Teste";
        final var aState = "Estado Teste";
        final var aZipCode = "12345678";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("district");
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCustomerAddressCommand.with(
                aAccountId,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode);

        Mockito.when(customerGateway.findByAccountId(aAccountId)).thenReturn(Optional.of(aCustomer));
        Mockito.when(addressAdapter.findAddressByZipCode(aZipCode))
                .thenReturn(Optional.of(makeAddressResponse()));

        final var aResult = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aAccountId);
        Mockito.verify(addressAdapter, Mockito.times(1)).findAddressByZipCode(aZipCode);
        Mockito.verify(addressGateway, Mockito.times(0)).deleteById(Mockito.any());
        Mockito.verify(customerGateway, Mockito.times(0)).update(Mockito.any());
        Mockito.verify(customerCacheGateway, Mockito.times(0)).delete(aAccountId);
    }

    @Test
    void givenAnInvalidNullCity_whenCallChangeAddress_shouldReturnDomainException() {
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aAccountId = aCustomer.getAccountId();

        final var aStreet = "Rua Teste";
        final var aNumber = "123";
        final var aComplement = "Complemento Teste";
        final var aDistrict = "Bairro Teste";
        final String aCity = null;
        final var aState = "Estado Teste";
        final var aZipCode = "12345678";

        final var expectedErrorMessage = "'city' not exists";
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCustomerAddressCommand.with(
                aAccountId,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode);

        Mockito.when(customerGateway.findByAccountId(aAccountId)).thenReturn(Optional.of(aCustomer));
        Mockito.when(addressAdapter.findAddressByZipCode(aZipCode))
                .thenReturn(Optional.of(makeAddressResponse()));

        final var aResult = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aAccountId);
        Mockito.verify(addressAdapter, Mockito.times(1)).findAddressByZipCode(aZipCode);
        Mockito.verify(addressGateway, Mockito.times(0)).deleteById(Mockito.any());
        Mockito.verify(customerGateway, Mockito.times(0)).update(Mockito.any());
        Mockito.verify(customerCacheGateway, Mockito.times(0)).delete(aAccountId);
    }

    @Test
    void givenAnInvalidNullState_whenCallChangeAddress_shouldReturnDomainException() {
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aAccountId = aCustomer.getAccountId();

        final var aStreet = "Rua Teste";
        final var aNumber = "123";
        final var aComplement = "Complemento Teste";
        final var aDistrict = "Bairro Teste";
        final var aCity = "Cidade Teste";
        final String aState = null;
        final var aZipCode = "12345678";

        final var expectedErrorMessage = "'state' not exists";
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCustomerAddressCommand.with(
                aAccountId,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode);

        Mockito.when(customerGateway.findByAccountId(aAccountId)).thenReturn(Optional.of(aCustomer));
        Mockito.when(addressAdapter.findAddressByZipCode(aZipCode))
                .thenReturn(Optional.of(makeAddressResponse()));

        final var aResult = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aAccountId);
        Mockito.verify(addressAdapter, Mockito.times(1)).findAddressByZipCode(aZipCode);
        Mockito.verify(addressGateway, Mockito.times(0)).deleteById(Mockito.any());
        Mockito.verify(customerGateway, Mockito.times(0)).update(Mockito.any());
        Mockito.verify(customerCacheGateway, Mockito.times(0)).delete(aAccountId);
    }

    @Test
    void givenAnInvalidNullZipCode_whenCallChangeAddress_shouldReturnDomainException() {
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aAccountId = aCustomer.getAccountId();

        final var aStreet = "Rua Teste";
        final var aNumber = "123";
        final var aComplement = "Complemento Teste";
        final var aDistrict = "Bairro Teste";
        final var aCity = "Cidade Teste";
        final var aState = "Estado Teste";
        final String aZipCode = null;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("zipCode");
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCustomerAddressCommand.with(
                aAccountId,
                aStreet,
                aNumber,
                aComplement,
                aDistrict,
                aCity,
                aState,
                aZipCode);

        Mockito.when(customerGateway.findByAccountId(aAccountId)).thenReturn(Optional.of(aCustomer));
        Mockito.when(addressAdapter.findAddressByZipCode(aZipCode))
                .thenReturn(Optional.of(makeAddressResponse()));

        final var aResult = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aAccountId);
        Mockito.verify(addressAdapter, Mockito.times(1)).findAddressByZipCode(aZipCode);
        Mockito.verify(addressGateway, Mockito.times(0)).deleteById(Mockito.any());
        Mockito.verify(customerGateway, Mockito.times(0)).update(Mockito.any());
        Mockito.verify(customerCacheGateway, Mockito.times(0)).delete(aAccountId);
    }

    private AddressResponse makeAddressResponse() {
        final var aStreet = "Rua Teste";
        final var aComplement = "Complemento Teste";
        final var aDistrict = "Bairro Teste";
        final var aCity = "Cidade Teste";
        final var aState = "Estado Teste";
        final var aZipCode = "12345678";
        return new AddressResponse(
                aZipCode,
                aStreet,
                aComplement,
                aDistrict,
                aCity,
                aState
        );
    }
}
