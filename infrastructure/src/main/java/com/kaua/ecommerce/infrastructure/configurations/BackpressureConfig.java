package com.kaua.ecommerce.infrastructure.configurations;

import org.elasticsearch.client.RestClient;
import org.springframework.boot.actuate.data.redis.RedisHealthIndicator;
import org.springframework.boot.actuate.elasticsearch.ElasticsearchRestClientHealthIndicator;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import javax.sql.DataSource;

@Configuration
public class BackpressureConfig {

    @Bean
    public RedisHealthIndicator cacheHealthIndicator(RedisConnectionFactory redisConnectionFactory) {
        return new RedisHealthIndicator(redisConnectionFactory);
    }

    @Bean
    public DataSourceHealthIndicator dataSourceHealthIndicator(DataSource dataSource) {
        return new DataSourceHealthIndicator(dataSource, "SELECT 1");
    }

    @Bean
    public ElasticsearchRestClientHealthIndicator elasticsearchHealthIndicator(RestClient restClient) {
        return new ElasticsearchRestClientHealthIndicator(restClient);
    }
}
