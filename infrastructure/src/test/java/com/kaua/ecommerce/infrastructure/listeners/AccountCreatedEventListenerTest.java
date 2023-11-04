package com.kaua.ecommerce.infrastructure.listeners;

import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.usecases.customer.create.CreateCustomerCommand;
import com.kaua.ecommerce.application.usecases.customer.create.CreateCustomerOutput;
import com.kaua.ecommerce.application.usecases.customer.create.CreateCustomerUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;
import com.kaua.ecommerce.infrastructure.AmqpTest;
import com.kaua.ecommerce.infrastructure.configurations.annotations.AccountCreatedEvent;
import com.kaua.ecommerce.infrastructure.configurations.json.Json;
import com.kaua.ecommerce.infrastructure.configurations.properties.amqp.QueueProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.Serializable;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@AmqpTest
public class AccountCreatedEventListenerTest {

    @Autowired
    private TestRabbitTemplate testRabbitTemplate;

    @Autowired
    private RabbitListenerTestHarness harness;

    @MockBean
    private CreateCustomerUseCase createCustomerUseCase;

    @Autowired
    @AccountCreatedEvent
    private QueueProperties queueProperties;

    @Test
    void givenAValidAccountCreatedEvent_whenReceiveMessage_thenShouldCreateCustomer() throws InterruptedException {
        final var aAccountCreatedEvent = new AccountCreatedEventDummy(
                "123",
                "teste",
                "Testes",
                "teste.testes@tesss.com",
                InstantUtils.now()
        );
        final var aCustomer = Fixture.Customers.customerDefault;

        final var expectedMessage = Json.writeValueAsString(aAccountCreatedEvent);

        Mockito.when(createCustomerUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(CreateCustomerOutput.from(aCustomer)));

        this.testRabbitTemplate.convertAndSend(queueProperties.getQueue(), expectedMessage);

        final var invocationData =
                harness.getNextInvocationDataFor(
                        AccountCreatedEventListener.ACCOUNT_CREATED_LISTENER,
                        1, TimeUnit.SECONDS);

        Assertions.assertNotNull(invocationData);
        Assertions.assertNotNull(invocationData.getArguments());

        final var message = (Message) invocationData.getArguments()[0];
        final var actualMessage = new String(message.getBody());
        Assertions.assertEquals(expectedMessage, actualMessage);

        final var cmdCaptor = ArgumentCaptor.forClass(CreateCustomerCommand.class);
        Mockito.verify(createCustomerUseCase, Mockito.times(1))
                .execute(cmdCaptor.capture());

        final var actualCommand = cmdCaptor.getValue();
        Assertions.assertEquals(aAccountCreatedEvent.id(), actualCommand.accountId());
        Assertions.assertEquals(aAccountCreatedEvent.firstName(), actualCommand.firstName());
        Assertions.assertEquals(aAccountCreatedEvent.lastName(), actualCommand.lastName());
        Assertions.assertEquals(aAccountCreatedEvent.email(), actualCommand.email());
    }

    @Test
    void givenAnInvalidDuplicateAccountCreatedEvent_whenReceiveMessage_shouldRejectAndPurgeMessage() throws InterruptedException {
        final var aAccountCreatedEvent = new AccountCreatedEventDummy(
                "123",
                "teste",
                "Testes",
                "teste.testes@tesss.com",
                InstantUtils.now()
        );

        final var expectedMessage = Json.writeValueAsString(aAccountCreatedEvent);

        Mockito.when(createCustomerUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error("Customer already exists with this account id"))));

        this.testRabbitTemplate.convertAndSend(queueProperties.getQueue(), expectedMessage);

        final var invocationData =
                harness.getNextInvocationDataFor(
                        AccountCreatedEventListener.ACCOUNT_CREATED_LISTENER,
                        1, TimeUnit.SECONDS);

        Assertions.assertNotNull(invocationData);
        Assertions.assertNotNull(invocationData.getArguments());

        final var message = (Message) invocationData.getArguments()[0];
        final var actualMessage = new String(message.getBody());
        Assertions.assertEquals(expectedMessage, actualMessage);

        final var cmdCaptor = ArgumentCaptor.forClass(CreateCustomerCommand.class);
        Mockito.verify(createCustomerUseCase, Mockito.times(1))
                .execute(cmdCaptor.capture());

        final var actualCommand = cmdCaptor.getValue();
        Assertions.assertEquals(aAccountCreatedEvent.id(), actualCommand.accountId());
        Assertions.assertEquals(aAccountCreatedEvent.firstName(), actualCommand.firstName());
        Assertions.assertEquals(aAccountCreatedEvent.lastName(), actualCommand.lastName());
        Assertions.assertEquals(aAccountCreatedEvent.email(), actualCommand.email());
    }

    @Test
    void givenAnInvalidFirstNameLengthAccountCreatedEvent_whenReceiveMessage_thenShouldCreateCustomer() throws InterruptedException {
        final var aAccountCreatedEvent = new AccountCreatedEventDummy(
                "123",
                "teste",
                "Testes",
                "teste.testes@tesss.com",
                InstantUtils.now()
        );

        final var expectedMessage = Json.writeValueAsString(aAccountCreatedEvent);

        Mockito.when(createCustomerUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler
                        .create(new Error(CommonErrorMessage.nullOrBlank("firstName")))));

        this.testRabbitTemplate.convertAndSend(queueProperties.getQueue(), expectedMessage);

        final var invocationData =
                harness.getNextInvocationDataFor(
                        AccountCreatedEventListener.ACCOUNT_CREATED_LISTENER,
                        1, TimeUnit.SECONDS);

        Assertions.assertNotNull(invocationData);
        Assertions.assertNotNull(invocationData.getArguments());

        final var message = (Message) invocationData.getArguments()[0];
        final var actualMessage = new String(message.getBody());
        Assertions.assertEquals(expectedMessage, actualMessage);

        final var cmdCaptor = ArgumentCaptor.forClass(CreateCustomerCommand.class);
        Mockito.verify(createCustomerUseCase, Mockito.times(1))
                .execute(cmdCaptor.capture());

        final var actualCommand = cmdCaptor.getValue();
        Assertions.assertEquals(aAccountCreatedEvent.id(), actualCommand.accountId());
        Assertions.assertEquals(aAccountCreatedEvent.firstName(), actualCommand.firstName());
        Assertions.assertEquals(aAccountCreatedEvent.lastName(), actualCommand.lastName());
        Assertions.assertEquals(aAccountCreatedEvent.email(), actualCommand.email());
    }

    public record AccountCreatedEventDummy(
            String id,
            String firstName,
            String lastName,
            String email,
            Instant occurredOn
    ) implements Serializable {}
}
