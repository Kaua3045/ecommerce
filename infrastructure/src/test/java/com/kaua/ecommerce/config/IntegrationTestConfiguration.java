package com.kaua.ecommerce.config;

import com.kaua.ecommerce.infrastructure.category.persistence.CategoryElasticsearchRepository;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;

public class IntegrationTestConfiguration {

    @Bean
    public CategoryElasticsearchRepository categoryElasticsearchRepository() {
        return Mockito.mock(CategoryElasticsearchRepository.class);
    }
}