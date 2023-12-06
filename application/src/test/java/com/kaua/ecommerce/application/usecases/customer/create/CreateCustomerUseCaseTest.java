package com.kaua.ecommerce.application.usecases.customer.create;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.CustomerGateway;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Objects;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

public class CreateCustomerUseCaseTest extends UseCaseTest {

    @Mock
    private CustomerGateway customerGateway;

    @InjectMocks
    private DefaultCreateCustomerUseCase useCase;

    @Test
    void givenAValidCommand_whenCallCreateCustomer_shouldReturnACustomerIdCreated() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final var aCommand = CreateCustomerCommand.with(aAccountId, aFirstName, aLastName, aEmail);

        Mockito.when(customerGateway.existsByAccountId(aAccountId)).thenReturn(false);
        Mockito.when(customerGateway.create(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = useCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.id());

        Mockito.verify(customerGateway, Mockito.times(1)).existsByAccountId(aAccountId);
        Mockito.verify(customerGateway, Mockito.times(1)).create(argThat(aCustomer ->
                Objects.nonNull(aCustomer.getId()) &&
                Objects.equals(aAccountId, aCustomer.getAccountId()) &&
                Objects.equals(aFirstName, aCustomer.getFirstName()) &&
                Objects.equals(aLastName, aCustomer.getLastName()) &&
                Objects.equals(aEmail, aCustomer.getEmail()) &&
                Objects.nonNull(aCustomer.getCreatedAt()) &&
                Objects.nonNull(aCustomer.getUpdatedAt())));
    }

    @Test
    void givenAnExistsAccountId_whenCallCreateCustomer_shouldReturnAnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final var expectedErrorMessage = "Customer already exists with this account id";
        final var expectedErrorCount = 1;

        final var aCommand = CreateCustomerCommand.with(aAccountId, aFirstName, aLastName, aEmail);

        Mockito.when(customerGateway.existsByAccountId(aAccountId)).thenReturn(true);

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(customerGateway, Mockito.times(1)).existsByAccountId(aAccountId);
        Mockito.verify(customerGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidNullAccountId_whenCallCreateCustomer_shouldReturnAnDomainException() {
        final String aAccountId = null;
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("accountId");
        final var expectedErrorCount = 1;

        final var aCommand = CreateCustomerCommand.with(aAccountId, aFirstName, aLastName, aEmail);

        Mockito.when(customerGateway.existsByAccountId(aAccountId)).thenReturn(false);

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(customerGateway, Mockito.times(1)).existsByAccountId(aAccountId);
        Mockito.verify(customerGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidBlankAccountId_whenCallCreateCustomer_shouldReturnAnDomainException() {
        final var aAccountId = " ";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("accountId");
        final var expectedErrorCount = 1;

        final var aCommand = CreateCustomerCommand.with(aAccountId, aFirstName, aLastName, aEmail);

        Mockito.when(customerGateway.existsByAccountId(aAccountId)).thenReturn(false);

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(customerGateway, Mockito.times(1)).existsByAccountId(aAccountId);
        Mockito.verify(customerGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidNullFirstName_whenCallCreateCustomer_shouldReturnAnDomainException() {
        final var aAccountId = "123456789";
        final String aFirstName = null;
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("firstName");
        final var expectedErrorCount = 1;

        final var aCommand = CreateCustomerCommand.with(aAccountId, aFirstName, aLastName, aEmail);

        Mockito.when(customerGateway.existsByAccountId(aAccountId)).thenReturn(false);

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(customerGateway, Mockito.times(1)).existsByAccountId(aAccountId);
        Mockito.verify(customerGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidBlankFirstName_whenCallCreateCustomer_shouldReturnAnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = " ";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("firstName");
        final var expectedErrorCount = 1;

        final var aCommand = CreateCustomerCommand.with(aAccountId, aFirstName, aLastName, aEmail);

        Mockito.when(customerGateway.existsByAccountId(aAccountId)).thenReturn(false);

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(customerGateway, Mockito.times(1)).existsByAccountId(aAccountId);
        Mockito.verify(customerGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidFirstNameLengthLessThan3_whenCallCreateCustomer_shouldReturnAnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = "Te";
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("firstName", 3, 255);
        final var expectedErrorCount = 1;

        final var aCommand = CreateCustomerCommand.with(aAccountId, aFirstName, aLastName, aEmail);

        Mockito.when(customerGateway.existsByAccountId(aAccountId)).thenReturn(false);

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(customerGateway, Mockito.times(1)).existsByAccountId(aAccountId);
        Mockito.verify(customerGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidFirstNameLengthMoreThan255_whenCallCreateCustomer_shouldReturnAnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = RandomStringUtils.generateValue(256);
        final var aLastName = "Testes";
        final var aEmail = "teste.testes@fakte.com";

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("firstName", 3, 255);
        final var expectedErrorCount = 1;

        final var aCommand = CreateCustomerCommand.with(aAccountId, aFirstName, aLastName, aEmail);

        Mockito.when(customerGateway.existsByAccountId(aAccountId)).thenReturn(false);

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(customerGateway, Mockito.times(1)).existsByAccountId(aAccountId);
        Mockito.verify(customerGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidNullLastName_whenCallCreateCustomer_shouldReturnAnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final String aLastName = null;
        final var aEmail = "teste.testes@fakte.com";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("lastName");
        final var expectedErrorCount = 1;

        final var aCommand = CreateCustomerCommand.with(aAccountId, aFirstName, aLastName, aEmail);

        Mockito.when(customerGateway.existsByAccountId(aAccountId)).thenReturn(false);

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(customerGateway, Mockito.times(1)).existsByAccountId(aAccountId);
        Mockito.verify(customerGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidBlankLastName_whenCallCreateCustomer_shouldReturnAnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = "Testes";
        final var aLastName = " ";
        final var aEmail = "teste.testes@fakte.com";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("lastName");
        final var expectedErrorCount = 1;

        final var aCommand = CreateCustomerCommand.with(aAccountId, aFirstName, aLastName, aEmail);

        Mockito.when(customerGateway.existsByAccountId(aAccountId)).thenReturn(false);

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(customerGateway, Mockito.times(1)).existsByAccountId(aAccountId);
        Mockito.verify(customerGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidLastNameLengthLessThan3_whenCallCreateCustomer_shouldReturnAnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = "Testes";
        final var aLastName = "Te ";
        final var aEmail = "teste.testes@fakte.com";

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("lastName", 3, 255);
        final var expectedErrorCount = 1;

        final var aCommand = CreateCustomerCommand.with(aAccountId, aFirstName, aLastName, aEmail);

        Mockito.when(customerGateway.existsByAccountId(aAccountId)).thenReturn(false);

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(customerGateway, Mockito.times(1)).existsByAccountId(aAccountId);
        Mockito.verify(customerGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidLastNameLengthMoreThan255_whenCallCreateCustomer_shouldReturnAnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = "Testes";
        final var aLastName = RandomStringUtils.generateValue(256);
        final var aEmail = "teste.testes@fakte.com";

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("lastName", 3, 255);
        final var expectedErrorCount = 1;

        final var aCommand = CreateCustomerCommand.with(aAccountId, aFirstName, aLastName, aEmail);

        Mockito.when(customerGateway.existsByAccountId(aAccountId)).thenReturn(false);

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(customerGateway, Mockito.times(1)).existsByAccountId(aAccountId);
        Mockito.verify(customerGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidNullEmail_whenCallCreateCustomer_shouldReturnAnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = "Teste";
        final var aLastName = "Testes";
        final String aEmail = null;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("email");
        final var expectedErrorCount = 1;

        final var aCommand = CreateCustomerCommand.with(aAccountId, aFirstName, aLastName, aEmail);

        Mockito.when(customerGateway.existsByAccountId(aAccountId)).thenReturn(false);

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(customerGateway, Mockito.times(1)).existsByAccountId(aAccountId);
        Mockito.verify(customerGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidBlankEmail_whenCallCreateCustomer_shouldReturnAnDomainException() {
        final var aAccountId = "123456789";
        final var aFirstName = "Testes";
        final var aLastName = "Teste";
        final var aEmail = " ";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("email");
        final var expectedErrorCount = 1;

        final var aCommand = CreateCustomerCommand.with(aAccountId, aFirstName, aLastName, aEmail);

        Mockito.when(customerGateway.existsByAccountId(aAccountId)).thenReturn(false);

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(customerGateway, Mockito.times(1)).existsByAccountId(aAccountId);
        Mockito.verify(customerGateway, Mockito.times(0)).create(Mockito.any());
    }
}
