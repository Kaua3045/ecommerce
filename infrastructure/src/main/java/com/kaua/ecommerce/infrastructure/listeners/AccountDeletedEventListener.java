package com.kaua.ecommerce.infrastructure.listeners;

import com.kaua.ecommerce.application.usecases.customer.delete.DeleteCustomerUseCase;
import com.kaua.ecommerce.infrastructure.configurations.json.Json;
import com.kaua.ecommerce.infrastructure.customer.models.DeleteCustomerListener;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Profile("!development")
public class AccountDeletedEventListener {

    public static final String ACCOUNT_DELETED_LISTENER = "AccountDeletedListener";
    private static final String ACCOUNT_DELETED_QUEUE = "${amqp.queues.account-deleted.queue}";

    private final DeleteCustomerUseCase deleteCustomerUseCase;

    public AccountDeletedEventListener(final DeleteCustomerUseCase deleteCustomerUseCase) {
        this.deleteCustomerUseCase = Objects.requireNonNull(deleteCustomerUseCase);
    }

    @RabbitListener(id = ACCOUNT_DELETED_LISTENER, queues = ACCOUNT_DELETED_QUEUE)
    public void onAccountCreated(@Payload final Message message) {
        final var aAccount = Json.readValue(new String(message.getBody()), DeleteCustomerListener.class);

        this.deleteCustomerUseCase.execute(aAccount.id());
    }
}
