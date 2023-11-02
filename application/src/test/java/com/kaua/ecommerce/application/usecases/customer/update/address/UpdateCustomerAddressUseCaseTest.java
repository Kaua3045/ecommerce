package com.kaua.ecommerce.application.usecases.customer.update.address;

import com.kaua.ecommerce.application.adapters.AddressAdapter;
import com.kaua.ecommerce.application.adapters.responses.AddressResponse;
import com.kaua.ecommerce.application.gateways.AddressGateway;
import com.kaua.ecommerce.application.gateways.CustomerGateway;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.customer.address.Address;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

@ExtendWith(MockitoExtension.class)
public class UpdateCustomerAddressUseCaseTest {

    @Mock
    private CustomerGateway customerGateway;

    @Mock
    private AddressAdapter addressAdapter;

    @Mock
    private AddressGateway addressGateway;

    @InjectMocks
    private DefaultUpdateCustomerAddressUseCase useCase;

    @Test
    void givenAValidCommand_whenCallChangeAddress_shouldReturnAnAccountId() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final var aStreet = "Rua Teste";
        final var aNumber = "123";
        final var aComplement = "Complemento Teste";
        final var aDistrict = "Bairro Teste";
        final var aCity = "Cidade Teste";
        final var aState = "Estado Teste";
        final var aZipCode = "12345678";

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

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
        Mockito.when(customerGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aResult = useCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aResult);
        Assertions.assertEquals(aAccountId, aResult.accountId());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aAccountId);
        Mockito.verify(addressAdapter, Mockito.times(1)).findAddressByZipCode(aZipCode);
        Mockito.verify(addressGateway, Mockito.times(1)).deleteById(Mockito.any());
        Mockito.verify(customerGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aAccountId, aCmd.getAccountId())
                && Objects.equals(aCustomer.getId(), aCmd.getId())
                && Objects.equals(aFirstName, aCmd.getFirstName())
                && Objects.equals(aLastName, aCmd.getLastName())
                && Objects.equals(aEmail, aCmd.getEmail())
                && Objects.isNull(aCmd.getCpf())
                && Objects.isNull(aCmd.getTelephone())
                && Objects.equals(aStreet, aCmd.getAddress().getStreet())
                && Objects.equals(aNumber, aCmd.getAddress().getNumber())
                && Objects.equals(aComplement, aCmd.getAddress().getComplement())
                && Objects.equals(aDistrict, aCmd.getAddress().getDistrict())
                && Objects.equals(aCity, aCmd.getAddress().getCity())
                && Objects.equals(aState, aCmd.getAddress().getState())
                && Objects.equals(aZipCode, aCmd.getAddress().getZipCode())
                && Objects.equals(aCustomer.getCreatedAt(), aCmd.getCreatedAt())
                && Objects.equals(aCustomer.getUpdatedAt(), aCmd.getUpdatedAt())));
    }

    @Test
    void givenAValidCommandAndCustomerWithAddress_whenCallChangeAddress_shouldReturnAnAccountId() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final var aAddress = Address.newAddress(
                "Rua Teste",
                "123",
                "Complemento Teste",
                "Bairro Teste",
                "Cidade Teste",
                "Estado Teste",
                "12345678"
        );

        final var aStreet = "Rua Teste";
        final var aNumber = "123";
        final var aComplement = "Complemento Teste";
        final var aDistrict = "Bairro Teste";
        final var aCity = "Cidade Teste";
        final var aState = "Estado Teste";
        final var aZipCode = "12345678";

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail)
                .changeAddress(aAddress);

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

        final var aResult = useCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aResult);
        Assertions.assertEquals(aAccountId, aResult.accountId());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aAccountId);
        Mockito.verify(addressAdapter, Mockito.times(1)).findAddressByZipCode(aZipCode);
        Mockito.verify(addressGateway, Mockito.times(1)).deleteById(Mockito.any());
        Mockito.verify(customerGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aAccountId, aCmd.getAccountId())
                        && Objects.equals(aCustomer.getId(), aCmd.getId())
                        && Objects.equals(aFirstName, aCmd.getFirstName())
                        && Objects.equals(aLastName, aCmd.getLastName())
                        && Objects.equals(aEmail, aCmd.getEmail())
                        && Objects.isNull(aCmd.getCpf())
                        && Objects.isNull(aCmd.getTelephone())
                        && Objects.equals(aStreet, aCmd.getAddress().getStreet())
                        && Objects.equals(aNumber, aCmd.getAddress().getNumber())
                        && Objects.equals(aComplement, aCmd.getAddress().getComplement())
                        && Objects.equals(aDistrict, aCmd.getAddress().getDistrict())
                        && Objects.equals(aCity, aCmd.getAddress().getCity())
                        && Objects.equals(aState, aCmd.getAddress().getState())
                        && Objects.equals(aZipCode, aCmd.getAddress().getZipCode())
                        && Objects.equals(aCustomer.getCreatedAt(), aCmd.getCreatedAt())
                        && Objects.equals(aCustomer.getUpdatedAt(), aCmd.getUpdatedAt())));
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

        final var expectedErrorMessage = "Customer with id 123456789 was not found";

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
    }

    @Test
    void givenAnInvalidZipCode_whenCallChangeAddress_shouldReturnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final var aStreet = "Rua Teste";
        final var aNumber = "123";
        final var aComplement = "Complemento Teste";
        final var aDistrict = "Bairro Teste";
        final var aCity = "Cidade Teste";
        final var aState = "Estado Teste";
        final var aZipCode = "12345678";

        final var expectedErrorMessage = "'zipCode' not exists";
        final var expectedErrorCount = 1;

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

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
    }

    @Test
    void givenAnInvalidNullStreet_whenCallChangeAddress_shouldReturnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final String aStreet = null;
        final var aNumber = "123";
        final var aComplement = "Complemento Teste";
        final var aDistrict = "Bairro Teste";
        final var aCity = "Cidade Teste";
        final var aState = "Estado Teste";
        final var aZipCode = "12345678";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("street");
        final var expectedErrorCount = 1;

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

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
    }

    @Test
    void givenAnInvalidNullNumber_whenCallChangeAddress_shouldReturnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final var aStreet = "Rua Teste";
        final String aNumber = null;
        final var aComplement = "Complemento Teste";
        final var aDistrict = "Bairro Teste";
        final var aCity = "Cidade Teste";
        final var aState = "Estado Teste";
        final var aZipCode = "12345678";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("number");
        final var expectedErrorCount = 1;

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

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
    }

    @Test
    void givenAnInvalidNullDistrict_whenCallChangeAddress_shouldReturnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final var aStreet = "Rua Teste";
        final var aNumber = "123";
        final var aComplement = "Complemento Teste";
        final String aDistrict = null;
        final var aCity = "Cidade Teste";
        final var aState = "Estado Teste";
        final var aZipCode = "12345678";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("district");
        final var expectedErrorCount = 1;

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

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
    }

    @Test
    void givenAnInvalidNullCity_whenCallChangeAddress_shouldReturnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final var aStreet = "Rua Teste";
        final var aNumber = "123";
        final var aComplement = "Complemento Teste";
        final var aDistrict = "Bairro Teste";
        final String aCity = null;
        final var aState = "Estado Teste";
        final var aZipCode = "12345678";

        final var expectedErrorMessage = "'city' not exists";
        final var expectedErrorCount = 1;

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

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
    }

    @Test
    void givenAnInvalidNullState_whenCallChangeAddress_shouldReturnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final var aStreet = "Rua Teste";
        final var aNumber = "123";
        final var aComplement = "Complemento Teste";
        final var aDistrict = "Bairro Teste";
        final var aCity = "Cidade Teste";
        final String aState = null;
        final var aZipCode = "12345678";

        final var expectedErrorMessage = "'state' not exists";
        final var expectedErrorCount = 1;

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

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
    }

    @Test
    void givenAnInvalidNullZipCode_whenCallChangeAddress_shouldReturnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final var aStreet = "Rua Teste";
        final var aNumber = "123";
        final var aComplement = "Complemento Teste";
        final var aDistrict = "Bairro Teste";
        final var aCity = "Cidade Teste";
        final var aState = "Estado Teste";
        final String aZipCode = null;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("zipCode");
        final var expectedErrorCount = 1;

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

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
