package com.kaua.ecommerce.infrastructure.configurations;

import com.kaua.ecommerce.infrastructure.configurations.annotations.CategoryEvents;
import com.kaua.ecommerce.infrastructure.configurations.properties.kafka.KafkaTopicProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    @ConfigurationProperties(prefix = "ecommerce.topics.category")
    @CategoryEvents
    public KafkaTopicProperty categoryTopic() {
        return new KafkaTopicProperty();
    }
}
