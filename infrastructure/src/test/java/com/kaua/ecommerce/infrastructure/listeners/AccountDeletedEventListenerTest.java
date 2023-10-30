package com.kaua.ecommerce.infrastructure.listeners;

import com.kaua.ecommerce.application.usecases.customer.delete.DeleteCustomerUseCase;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.infrastructure.AmqpTest;
import com.kaua.ecommerce.infrastructure.configurations.annotations.AccountDeletedEvent;
import com.kaua.ecommerce.infrastructure.configurations.json.Json;
import com.kaua.ecommerce.infrastructure.configurations.properties.amqp.QueueProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
public class AccountDeletedEventListenerTest {

    @Autowired
    private TestRabbitTemplate testRabbitTemplate;

    @Autowired
    private RabbitListenerTestHarness harness;

    @MockBean
    private DeleteCustomerUseCase deleteCustomerUseCase;

    @Autowired
    @AccountDeletedEvent
    private QueueProperties queueProperties;

    @Test
    void givenAValidAccountDeletedEvent_whenReceiveMessage_thenShouldDeleteCustomer() throws InterruptedException {
        final var aAccountDeletedEvent = new AccountDeletedEventDummy(
                "123",
                InstantUtils.now()
        );

        final var expectedMessage = Json.writeValueAsString(aAccountDeletedEvent);

        Mockito.doNothing().when(deleteCustomerUseCase).execute(aAccountDeletedEvent.id());

        this.testRabbitTemplate.convertAndSend(queueProperties.getQueue(), expectedMessage);

        final var invocationData =
                harness.getNextInvocationDataFor(
                        AccountDeletedEventListener.ACCOUNT_DELETED_LISTENER,
                        1, TimeUnit.SECONDS);

        Assertions.assertNotNull(invocationData);
        Assertions.assertNotNull(invocationData.getArguments());

        final var message = (Message) invocationData.getArguments()[0];
        final var actualMessage = new String(message.getBody());
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito.verify(deleteCustomerUseCase, Mockito.times(1))
                .execute(aAccountDeletedEvent.id());
    }

    public record AccountDeletedEventDummy(
            String id,
            Instant occurredOn
    ) implements Serializable {}
}
