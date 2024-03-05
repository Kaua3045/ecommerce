package com.kaua.ecommerce.config;

import com.kaua.ecommerce.infrastructure.category.persistence.CategoryElasticsearchEntityRepository;
import com.kaua.ecommerce.infrastructure.product.persistence.elasticsearch.ProductElasticsearchEntityRepository;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;

public class IntegrationTestConfiguration {

    @Bean
    public CategoryElasticsearchEntityRepository categoryElasticsearchRepository() {
        return Mockito.mock(CategoryElasticsearchEntityRepository.class);
    }

    @Bean
    public ProductElasticsearchEntityRepository productElasticsearchRepository() {
        return Mockito.mock(ProductElasticsearchEntityRepository.class);
    }
}