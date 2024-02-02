package com.kaua.ecommerce.config;

import com.kaua.ecommerce.infrastructure.category.persistence.CategoryElasticsearchRepository;
import com.kaua.ecommerce.infrastructure.product.persistence.elasticsearch.ProductElasticsearchRepository;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;

public class IntegrationTestConfiguration {

    @Bean
    public CategoryElasticsearchRepository categoryElasticsearchRepository() {
        return Mockito.mock(CategoryElasticsearchRepository.class);
    }

    @Bean
    public ProductElasticsearchRepository productElasticsearchRepository() {
        return Mockito.mock(ProductElasticsearchRepository.class);
    }
}