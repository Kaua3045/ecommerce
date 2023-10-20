package com.kaua.ecommerce.infrastructure.configurations;

import com.kaua.ecommerce.infrastructure.configurations.annotations.AccountCreatedEvent;
import com.kaua.ecommerce.infrastructure.configurations.properties.amqp.QueueProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class AmqpConfig {

    @Bean
    @ConfigurationProperties("amqp.queues.account-created")
    @AccountCreatedEvent
    public QueueProperties accountCreatedQueueProperties() {
        return new QueueProperties();
    }

    @Configuration
    static class Admin {

        private static final Logger log = LoggerFactory.getLogger(Admin.class);

        @Bean
        @Profile("production")
        public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
            final var template = new RabbitTemplate(connectionFactory);
            template.setMandatory(true);
            template.setReturnsCallback(returned -> {
                log.info("Message returned: {}, body: {}", returned, new String(returned.getMessage().getBody()));
                // falhou, se retorno é porque falhou, pronto!
            });
            template.setConfirmCallback((correlationData, ack, cause) -> {
                if (!ack) {
                    log.info("Message failed: {}, cause: {}, ack: {}", correlationData, cause, ack);
                    // falhou a publicação no rabbitmq (timeout ou erro)
                }
                log.debug("Message confirmed: {}, ack: {}", correlationData, ack);
                // foi entregue (pode não ter sido enviada para uma routingkey)
            });
            return template;
        }
    }
}
