package com.kaua.ecommerce.infrastructure;

import com.kaua.ecommerce.config.AmqpTestConfiguration;
import com.kaua.ecommerce.config.IntegrationTestConfiguration;
import com.kaua.ecommerce.config.JpaCleanUpExtension;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ActiveProfiles("test-integration")
@EnableAutoConfiguration(exclude = ElasticsearchRepositoriesAutoConfiguration.class)
@SpringBootTest(classes = { Main.class, AmqpTestConfiguration.class, IntegrationTestConfiguration.class })
@ExtendWith(JpaCleanUpExtension.class)
@Tag("integrationTest")
public @interface IntegrationTest {
}
