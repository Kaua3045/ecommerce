package com.kaua.ecommerce.infrastructure;

import com.kaua.ecommerce.config.ElasticsearchTestContainer;
import com.kaua.ecommerce.infrastructure.initializer.CategoryElasticsearchInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.boot.test.autoconfigure.data.redis.AutoConfigureDataRedis;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collection;

@ActiveProfiles("test-integration")
@ComponentScan(
        basePackages = "com.kaua.ecommerce.infrastructure",
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*ElasticsearchGateway")
        }
)
@DataElasticsearchTest
@AutoConfigureDataRedis
@ImportTestcontainers(ElasticsearchTestContainer.class)
@Import(CategoryElasticsearchInitializer.class)
@Testcontainers
@Tag("integrationTest")
public abstract class AbstractElasticsearchTest {

    @Autowired
    private Collection<ElasticsearchRepository<?, ?>> repositories;

    @BeforeEach
    void cleanUp() {
        try {
            this.repositories.forEach(ElasticsearchRepository::deleteAll);
        } catch (Exception e) {
            System.out.println("Error while cleaning up Elasticsearch: " + e.getMessage());
        }
    }
}
