package com.kaua.ecommerce.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collection;

@ActiveProfiles("test-integration-elastic")
@DataElasticsearchTest
@ImportTestcontainers(ElasticsearchTestContainer.class)
@Testcontainers
public abstract class AbstractElasticsearchTest {

    @Autowired
    private Collection<ElasticsearchRepository<?, ?>> repositories;

    @BeforeEach
    void cleanUp() {
        this.repositories.forEach(ElasticsearchRepository::deleteAll);
    }
}
