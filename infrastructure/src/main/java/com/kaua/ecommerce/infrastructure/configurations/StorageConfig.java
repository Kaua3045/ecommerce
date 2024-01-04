package com.kaua.ecommerce.infrastructure.configurations;

import com.kaua.ecommerce.infrastructure.configurations.properties.aws.AwsS3StorageProperties;
import com.kaua.ecommerce.infrastructure.configurations.properties.storage.StorageProperties;
import com.kaua.ecommerce.infrastructure.service.StorageService;
import com.kaua.ecommerce.infrastructure.service.impl.S3StorageServiceImpl;
import com.kaua.ecommerce.infrastructure.service.local.InMemoryStorageServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class StorageConfig {

    @Bean
    @ConfigurationProperties(prefix = "storage.aws.s3.product")
    public StorageProperties awsS3StoragePropertiesProduct() {
        return new StorageProperties();
    }

    @Bean
    @Profile({"development", "production"})
    public StorageService storageServiceS3(
            final AwsS3StorageProperties properties,
            final S3Client s3Client
    ) {
        return new S3StorageServiceImpl(properties.getBucketName(), s3Client);
    }

    @Bean
    @ConditionalOnMissingBean
    public StorageService storageServiceLocal() {
        return new InMemoryStorageServiceImpl();
    }
}
