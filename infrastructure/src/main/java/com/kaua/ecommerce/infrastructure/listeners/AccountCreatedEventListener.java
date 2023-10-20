package com.kaua.ecommerce.infrastructure.listeners;

import com.kaua.ecommerce.application.usecases.customer.create.CreateCustomerCommand;
import com.kaua.ecommerce.application.usecases.customer.create.CreateCustomerUseCase;
import com.kaua.ecommerce.infrastructure.configurations.json.Json;
import com.kaua.ecommerce.infrastructure.customer.models.CreateCustomerListener;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
@Profile("!development")
public class AccountCreatedEventListener {

    public static final String ACCOUNT_CREATED_LISTENER = "AccountCreatedListener";
    private static final String ACCOUNT_CREATED_QUEUE = "${amqp.queues.account-created.queue}";
    private static final String ACCOUNT_ID_ALREADY_EXISTS = "Customer already exists with this account id";

    private final CreateCustomerUseCase createCustomerUseCase;

    public AccountCreatedEventListener(final CreateCustomerUseCase createCustomerUseCase) {
        this.createCustomerUseCase = Objects.requireNonNull(createCustomerUseCase);
    }

    @RabbitListener(id = ACCOUNT_CREATED_LISTENER, queues = ACCOUNT_CREATED_QUEUE)
    public void onAccountCreated(@Payload final Message message, final Channel channel) throws IOException {
        final var aAccount = Json.readValue(new String(message.getBody()), CreateCustomerListener.class);

        final var aCommand = CreateCustomerCommand.with(
                aAccount.id(),
                aAccount.firstName(),
                aAccount.lastName(),
                aAccount.email());

        final var aResult = this.createCustomerUseCase.execute(aCommand);

        // TODO: trocar depois para getFirstErrorMessage por exemplo e mandar para uma DDL queue a mesma coisa para os outros erros
        if (aResult.isLeft() && aResult.getLeft().getErrors().get(0).message().equalsIgnoreCase(ACCOUNT_ID_ALREADY_EXISTS)) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }
    }
}
