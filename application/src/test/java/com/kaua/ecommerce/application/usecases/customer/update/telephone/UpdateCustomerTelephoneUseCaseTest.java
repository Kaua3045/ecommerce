package com.kaua.ecommerce.application.usecases.customer.update.telephone;

import com.kaua.ecommerce.application.adapters.TelephoneAdapter;
import com.kaua.ecommerce.application.gateways.CustomerGateway;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.exceptions.DomainException;
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
public class UpdateCustomerTelephoneUseCaseTest {

    @Mock
    private CustomerGateway customerGateway;

    @Mock
    private TelephoneAdapter telephoneAdapter;

    @InjectMocks
    private DefaultUpdateCustomerTelephoneUseCase useCase;

    @Test
    void givenAValidCommand_whenCallChangeTelephone_shouldReturnAnAccountId() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";
        final var aTelephone = "+11234567890";

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

        final var aCommand = UpdateCustomerTelephoneCommand.with(aAccountId, aTelephone);

        Mockito.when(customerGateway.findByAccountId(aAccountId)).thenReturn(Optional.of(aCustomer));
        Mockito.when(telephoneAdapter.validate(aTelephone)).thenReturn(true);
        Mockito.when(customerGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aResult = useCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aResult);
        Assertions.assertEquals(aAccountId, aResult.accountId());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aAccountId);
        Mockito.verify(telephoneAdapter, Mockito.times(1)).validate(aTelephone);
        Mockito.verify(customerGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aAccountId, aCmd.getAccountId())
                && Objects.equals(aCustomer.getId(), aCmd.getId())
                && Objects.equals(aFirstName, aCmd.getFirstName())
                && Objects.equals(aLastName, aCmd.getLastName())
                && Objects.equals(aEmail, aCmd.getEmail())
                && Objects.equals(aTelephone, aCmd.getTelephone().getValue())
                && Objects.equals(aCustomer.getCreatedAt(), aCmd.getCreatedAt())
                && Objects.equals(aCustomer.getUpdatedAt(), aCmd.getUpdatedAt())));
    }

    @Test
    void givenAnInvalidAccountId_whenCallChangeTelephone_shouldThrowNotFoundException() {
        final var aAccountId = "123456789";
        final var aTelephone = "+11234567890";

        final var expectedErrorMessage = "Customer with id 123456789 was not found";

        final var aCommand = UpdateCustomerTelephoneCommand.with(aAccountId, aTelephone);

        Mockito.when(customerGateway.findByAccountId(aAccountId)).thenReturn(Optional.empty());

        final var aResult = Assertions.assertThrows(NotFoundException.class,
                () -> useCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aResult.getMessage());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aAccountId);
        Mockito.verify(telephoneAdapter, Mockito.times(0)).validate(aTelephone);
        Mockito.verify(customerGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidNullTelephone_whenCallChangeTelephone_shouldThrowDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";
        final String aTelephone = null;

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("telephone");
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCustomerTelephoneCommand.with(aAccountId, aTelephone);

        Mockito.when(customerGateway.findByAccountId(aAccountId)).thenReturn(Optional.of(aCustomer));
        Mockito.when(telephoneAdapter.validate(aTelephone)).thenReturn(true);

        final var aResult = Assertions.assertThrows(DomainException.class, () ->
                useCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aAccountId);
        Mockito.verify(telephoneAdapter, Mockito.times(1)).validate(aTelephone);
        Mockito.verify(customerGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidTelephone_whenCallChangeTelephone_shouldReturnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";
        final var aTelephone = "123456789";

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

        final var expectedErrorMessage = "'telephone' invalid";
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCustomerTelephoneCommand.with(aAccountId, aTelephone);

        Mockito.when(customerGateway.findByAccountId(aAccountId)).thenReturn(Optional.of(aCustomer));
        Mockito.when(telephoneAdapter.validate(aTelephone)).thenReturn(false);

        final var aResult = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aAccountId);
        Mockito.verify(telephoneAdapter, Mockito.times(1)).validate(aTelephone);
        Mockito.verify(customerGateway, Mockito.times(0)).update(Mockito.any());
    }
}
