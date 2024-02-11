package com.kaua.ecommerce.infrastructure;

import com.kaua.ecommerce.config.AmqpTestConfiguration;
import com.kaua.ecommerce.config.CacheCleanUpExtension;
import com.kaua.ecommerce.config.IntegrationTestConfiguration;
import com.kaua.ecommerce.config.JpaCleanUpExtension;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ActiveProfiles("test-integration-cache")
@EnableAutoConfiguration(exclude = ElasticsearchRepositoriesAutoConfiguration.class)
@SpringBootTest(classes = { Main.class, AmqpTestConfiguration.class, IntegrationTestConfiguration.class })
@ExtendWith({ CacheCleanUpExtension.class, JpaCleanUpExtension.class })
@DirtiesContext
@Tag("integrationTest")
public @interface CacheGatewayTest {
}
