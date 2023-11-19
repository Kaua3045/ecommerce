package com.kaua.ecommerce.infrastructure.configurations;

import com.kaua.ecommerce.infrastructure.service.EventService;
import com.kaua.ecommerce.infrastructure.service.impl.KafkaEventServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class EventServiceConfig {

    @Bean
    @ConditionalOnMissingBean
    public EventService kafkaEventService(KafkaTemplate<String, Object> kafkaTemplate) {
        return new KafkaEventServiceImpl(kafkaTemplate);
    }
}
