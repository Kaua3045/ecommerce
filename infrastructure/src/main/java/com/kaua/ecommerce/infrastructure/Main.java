package com.kaua.ecommerce.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableFeignClients
@EnableRedisRepositories
public class Main {
    public static void main(String[] args) {
        System.setProperty(AbstractEnvironment.DEFAULT_PROFILES_PROPERTY_NAME, "development");
        SpringApplication.run(Main.class, args);
    }
}