package com.kaua.ecommerce.application.usecases.customer.update.telephone;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.adapters.TelephoneAdapter;
import com.kaua.ecommerce.application.gateways.CacheGateway;
import com.kaua.ecommerce.application.gateways.CustomerGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
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

public class UpdateCustomerTelephoneUseCaseTest extends UseCaseTest {

    @Mock
    private CustomerGateway customerGateway;

    @Mock
    private TelephoneAdapter telephoneAdapter;

    @Mock
    private CacheGateway<Customer> customerCacheGateway;

    @InjectMocks
    private DefaultUpdateCustomerTelephoneUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(customerGateway, customerCacheGateway, telephoneAdapter);
    }

    @Test
    void givenAValidCommand_whenCallChangeTelephone_shouldReturnAnAccountId() {
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aAccountId = aCustomer.getAccountId();
        final var aTelephone = "+11234567890";

        final var aCommand = UpdateCustomerTelephoneCommand.with(aAccountId, aTelephone);

        Mockito.when(customerGateway.findByAccountId(aAccountId)).thenReturn(Optional.of(aCustomer));
        Mockito.when(telephoneAdapter.formatInternational(aTelephone)).thenReturn(aTelephone);
        Mockito.when(telephoneAdapter.validate(aTelephone)).thenReturn(true);
        Mockito.when(customerGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());
        Mockito.doNothing().when(customerCacheGateway).delete(aAccountId);

        final var aResult = useCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aResult);
        Assertions.assertEquals(aAccountId, aResult.accountId());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aAccountId);
        Mockito.verify(telephoneAdapter, Mockito.times(1)).formatInternational(aTelephone);
        Mockito.verify(telephoneAdapter, Mockito.times(1)).validate(aTelephone);
        Mockito.verify(customerGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aAccountId, aCmd.getAccountId())
                && Objects.equals(aCustomer.getId(), aCmd.getId())
                && Objects.equals(aCustomer.getFirstName(), aCmd.getFirstName())
                && Objects.equals(aCustomer.getLastName(), aCmd.getLastName())
                && Objects.equals(aCustomer.getEmail(), aCmd.getEmail())
                && Objects.equals(aTelephone, aCmd.getTelephone().get().getValue())
                && Objects.equals(aCustomer.getCreatedAt(), aCmd.getCreatedAt())
                && Objects.equals(aCustomer.getUpdatedAt(), aCmd.getUpdatedAt())));
        Mockito.verify(customerCacheGateway, Mockito.times(1)).delete(aAccountId);
    }

    @Test
    void givenAnInvalidAccountId_whenCallChangeTelephone_shouldThrowNotFoundException() {
        final var aAccountId = "123456789";
        final var aTelephone = "+11234567890";

        final var expectedErrorMessage = Fixture.notFoundMessage(Customer.class, aAccountId);

        final var aCommand = UpdateCustomerTelephoneCommand.with(aAccountId, aTelephone);

        Mockito.when(customerGateway.findByAccountId(aAccountId)).thenReturn(Optional.empty());

        final var aResult = Assertions.assertThrows(NotFoundException.class,
                () -> useCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aResult.getMessage());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aAccountId);
        Mockito.verify(telephoneAdapter, Mockito.times(0)).formatInternational(aTelephone);
        Mockito.verify(telephoneAdapter, Mockito.times(0)).validate(aTelephone);
        Mockito.verify(customerGateway, Mockito.times(0)).update(Mockito.any());
        Mockito.verify(customerCacheGateway, Mockito.times(0)).delete(aAccountId);
    }

    @Test
    void givenAnInvalidNullTelephone_whenCallChangeTelephone_shouldThrowDomainException() {
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aAccountId = aCustomer.getAccountId();
        final String aTelephone = null;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("telephone");
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCustomerTelephoneCommand.with(aAccountId, aTelephone);

        Mockito.when(customerGateway.findByAccountId(aAccountId)).thenReturn(Optional.of(aCustomer));
        Mockito.when(telephoneAdapter.formatInternational(aTelephone)).thenReturn(aTelephone);
        Mockito.when(telephoneAdapter.validate(aTelephone)).thenReturn(true);

        final var aResult = Assertions.assertThrows(DomainException.class, () ->
                useCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aAccountId);
        Mockito.verify(telephoneAdapter, Mockito.times(1)).validate(aTelephone);
        Mockito.verify(telephoneAdapter, Mockito.times(1)).validate(aTelephone);
        Mockito.verify(customerGateway, Mockito.times(0)).update(Mockito.any());
        Mockito.verify(customerCacheGateway, Mockito.times(0)).delete(aAccountId);
    }

    @Test
    void givenAnInvalidTelephone_whenCallChangeTelephone_shouldReturnDomainException() {
        final var aCustomer = Fixture.Customers.customerDefault;
        final var aAccountId = aCustomer.getAccountId();
        final var aTelephone = "123456789";

        final var expectedErrorMessage = "'telephone' invalid";
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCustomerTelephoneCommand.with(aAccountId, aTelephone);

        Mockito.when(customerGateway.findByAccountId(aAccountId)).thenReturn(Optional.of(aCustomer));
        Mockito.when(telephoneAdapter.formatInternational(aTelephone)).thenReturn(aTelephone);
        Mockito.when(telephoneAdapter.validate(aTelephone)).thenReturn(false);

        final var aResult = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());

        Mockito.verify(customerGateway, Mockito.times(1)).findByAccountId(aAccountId);
        Mockito.verify(telephoneAdapter, Mockito.times(1)).formatInternational(aTelephone);
        Mockito.verify(telephoneAdapter, Mockito.times(1)).validate(aTelephone);
        Mockito.verify(customerGateway, Mockito.times(0)).update(Mockito.any());
        Mockito.verify(customerCacheGateway, Mockito.times(0)).delete(aAccountId);
    }
}
