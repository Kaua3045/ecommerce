package com.kaua.ecommerce.infrastructure;

import com.kaua.ecommerce.config.AmqpTestConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ActiveProfiles("test")
@SpringBootTest(classes = { Main.class, AmqpTestConfiguration.class })
public @interface AmqpTest {
}
