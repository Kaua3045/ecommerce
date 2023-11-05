package com.kaua.ecommerce.application.usecases.customer.update.cpf;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.CacheGateway;
import com.kaua.ecommerce.application.gateways.CustomerGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

public class UpdateCustomerCpfUseCaseTest extends UseCaseTest {

    @Mock
    private CustomerGateway customerGateway;

    @Mock
    private CacheGateway<Customer> customerCacheGateway;

    @InjectMocks
    private DefaultUpdateCustomerCpfUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(customerGateway, customerCacheGateway);
    }

    @Test
    void givenAValidCommand_whenCallChangeCpf_shouldReturnAnAccountId() {
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aAccountId = aCustomer.getAccountId();
        final var aCpf = "502.123.670-99";
        final var aCleanedCpf = "50212367099";

        final var aCommand = UpdateCustomerCpfCommand.with(aAccountId, aCpf);

        Mockito.when(customerGateway.findByAccountId(aAccountId)).thenReturn(Optional.of(aCustomer));
        Mockito.when(customerGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());
        Mockito.doNothing().when(customerCacheGateway).delete(aAccountId);

        final var aResult = useCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aResult);
        Assertions.assertEquals(aAccountId, aResult.accountId());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aAccountId);
        Mockito.verify(customerGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aAccountId, aCmd.getAccountId())
                && Objects.equals(aCustomer.getId(), aCmd.getId())
                && Objects.equals(aCustomer.getFirstName(), aCmd.getFirstName())
                && Objects.equals(aCustomer.getLastName(), aCmd.getLastName())
                && Objects.equals(aCustomer.getEmail(), aCmd.getEmail())
                && Objects.equals(aCleanedCpf, aCmd.getCpf().get().getValue())
                && Objects.equals(aCustomer.getCreatedAt(), aCmd.getCreatedAt())
                && Objects.equals(aCustomer.getUpdatedAt(), aCmd.getUpdatedAt())));
        Mockito.verify(customerCacheGateway, Mockito.times(1)).delete(aAccountId);
    }

    @Test
    void givenAnInvalidAccountId_whenCallChangeCpf_shouldThrowNotFoundException() {
        final var aAccountId = "123456789";
        final var aCpf = "502.123.670-99";

        final var expectedErrorMessage = Fixture.notFoundMessage(Customer.class, aAccountId);

        final var aCommand = UpdateCustomerCpfCommand.with(aAccountId, aCpf);

        Mockito.when(customerGateway.findByAccountId(aAccountId)).thenReturn(Optional.empty());

        final var aResult = Assertions.assertThrows(NotFoundException.class,
                () -> useCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aResult.getMessage());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aAccountId);
        Mockito.verify(customerGateway, Mockito.times(0)).update(Mockito.any());
        Mockito.verify(customerCacheGateway, Mockito.times(0)).delete(aAccountId);
    }

    @Test
    void givenAnInvalidCpf_whenCallChangeCpf_shouldThrowDomainException() {
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aAccountId = "123456789";
        final var aCpf = "502.123.670-10";

        final var expectedErrorMessage = "'cpf' invalid";
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCustomerCpfCommand.with(aAccountId, aCpf);

        Mockito.when(customerGateway.findByAccountId(aAccountId)).thenReturn(Optional.of(aCustomer));

        final var aResult = Assertions.assertThrows(DomainException.class, () ->
                useCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aAccountId);
        Mockito.verify(customerGateway, Mockito.times(0)).update(Mockito.any());
        Mockito.verify(customerCacheGateway, Mockito.times(0)).delete(aAccountId);
    }
}
