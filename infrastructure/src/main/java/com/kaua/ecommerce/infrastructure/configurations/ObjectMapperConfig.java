package com.kaua.ecommerce.infrastructure.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaua.ecommerce.infrastructure.configurations.json.Json;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@JsonComponent
public class ObjectMapperConfig {

    @Bean
    @Primary
    public ObjectMapper mapper() {
        return Json.mapper();
    }
}
