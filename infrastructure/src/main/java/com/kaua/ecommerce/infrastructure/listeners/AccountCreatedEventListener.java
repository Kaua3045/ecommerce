package com.kaua.ecommerce.infrastructure.listeners;

import com.kaua.ecommerce.application.usecases.customer.create.CreateCustomerCommand;
import com.kaua.ecommerce.application.usecases.customer.create.CreateCustomerUseCase;
import com.kaua.ecommerce.infrastructure.configurations.json.Json;
import com.kaua.ecommerce.infrastructure.customer.models.CreateCustomerListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Profile("!development")
public class AccountCreatedEventListener {

    public static final String ACCOUNT_CREATED_LISTENER = "AccountCreatedListener";
    private static final String ACCOUNT_CREATED_QUEUE = "${amqp.queues.account-created.queue}";

    private final CreateCustomerUseCase createCustomerUseCase;

    public AccountCreatedEventListener(final CreateCustomerUseCase createCustomerUseCase) {
        this.createCustomerUseCase = Objects.requireNonNull(createCustomerUseCase);
    }

    @RabbitListener(id = ACCOUNT_CREATED_LISTENER, queues = ACCOUNT_CREATED_QUEUE)
    public void onAccountCreated(@Payload final String message) {
        final var aAccount = Json.readValue(message, CreateCustomerListener.class);

        final var aCommand = CreateCustomerCommand.with(
                aAccount.id(),
                aAccount.firstName(),
                aAccount.lastName(),
                aAccount.email());

        final var aResult = this.createCustomerUseCase.execute(aCommand);

        if (aResult.isLeft()) {
            // TODO: tratar o caso de erro no futuro
            return;
        }
    }
}
