package com.kaua.ecommerce.infrastructure.configurations;

import com.kaua.ecommerce.infrastructure.service.EventProducerService;
import com.kaua.ecommerce.infrastructure.service.impl.KafkaEventProducerServiceImpl;
import com.kaua.ecommerce.infrastructure.service.local.InMemoryEventProducerServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class EventServiceConfig {

    @Bean
    @Profile("!test-integration")
    public EventProducerService kafkaEventService(KafkaTemplate<String, Object> kafkaTemplate) {
        return new KafkaEventProducerServiceImpl(kafkaTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public EventProducerService inMemoryEventService() {
        return new InMemoryEventProducerServiceImpl();
    }
}
