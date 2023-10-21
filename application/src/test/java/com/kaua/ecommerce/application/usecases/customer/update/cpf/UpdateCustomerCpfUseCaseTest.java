package com.kaua.ecommerce.application.usecases.customer.update.cpf;

import com.kaua.ecommerce.application.gateways.CustomerGateway;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
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
public class UpdateCustomerCpfUseCaseTest {

    @Mock
    private CustomerGateway customerGateway;

    @InjectMocks
    private DefaultUpdateCustomerCpfUseCase useCase;

    @Test
    void givenAValidCommand_whenCallChangeCpf_shouldReturnAnAccountId() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";
        final var aCpf = "502.123.670-99";
        final var aCleanedCpf = "50212367099";

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

        final var aCommand = UpdateCustomerCpfCommand.with(aAccountId, aCpf);

        Mockito.when(customerGateway.findByAccountId(aAccountId)).thenReturn(Optional.of(aCustomer));
        Mockito.when(customerGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aResult = useCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aResult);
        Assertions.assertEquals(aAccountId, aResult.accountId());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aAccountId);
        Mockito.verify(customerGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aAccountId, aCmd.getAccountId())
                && Objects.equals(aCustomer.getId(), aCmd.getId())
                && Objects.equals(aFirstName, aCmd.getFirstName())
                && Objects.equals(aLastName, aCmd.getLastName())
                && Objects.equals(aEmail, aCmd.getEmail())
                && Objects.equals(aCleanedCpf, aCmd.getCpf())
                && Objects.equals(aCustomer.getCreatedAt(), aCmd.getCreatedAt())
                && Objects.equals(aCustomer.getUpdatedAt(), aCmd.getUpdatedAt())));
    }

    @Test
    void givenAnInvalidAccountId_whenCallChangeCpf_shouldThrowNotFoundException() {
        final var aAccountId = "123456789";
        final var aCpf = "502.123.670-99";

        final var expectedErrorMessage = "Customer with id 123456789 was not found";

        final var aCommand = UpdateCustomerCpfCommand.with(aAccountId, aCpf);

        Mockito.when(customerGateway.findByAccountId(aAccountId)).thenReturn(Optional.empty());

        final var aResult = Assertions.assertThrows(NotFoundException.class,
                () -> useCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aResult.getMessage());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aAccountId);
        Mockito.verify(customerGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidCpf_whenCallChangeCpf_shouldReturnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";
        final var aCpf = "502.123.670-10";

        final var aCustomer = Customer.newCustomer(aAccountId, aFirstName, aLastName, aEmail);

        final var expectedErrorMessage = "'cpf' invalid";
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCustomerCpfCommand.with(aAccountId, aCpf);

        Mockito.when(customerGateway.findByAccountId(aAccountId)).thenReturn(Optional.of(aCustomer));

        final var aResult = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aAccountId);
        Mockito.verify(customerGateway, Mockito.times(0)).update(Mockito.any());
    }
}
