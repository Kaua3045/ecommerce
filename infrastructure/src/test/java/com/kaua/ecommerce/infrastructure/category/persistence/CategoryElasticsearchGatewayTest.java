package com.kaua.ecommerce.infrastructure.category.persistence;

import com.kaua.ecommerce.infrastructure.AbstractElasticsearchTest;
import com.kaua.ecommerce.infrastructure.category.CategoryEntityElastic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CategoryElasticsearchGatewayTest extends AbstractElasticsearchTest {

    @Autowired
    private CategoryEntityElastic categoryElasticsearchRepository;

    @Test
    void testInjection() {
        Assertions.assertNotNull(this.categoryElasticsearchRepository);
    }
}
