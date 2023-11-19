package com.kaua.ecommerce.config;

import com.kaua.ecommerce.infrastructure.category.persistence.CategoryElasticsearchRepository;
import com.kaua.ecommerce.infrastructure.service.local.InMemoryEventServiceImpl;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class DatabaseTestConfiguration {

    @Bean
    public InMemoryEventServiceImpl inMemoryEventService() {
        return new InMemoryEventServiceImpl();
    }
}