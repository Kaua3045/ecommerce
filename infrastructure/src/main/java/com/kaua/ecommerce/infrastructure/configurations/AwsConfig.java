package com.kaua.ecommerce.infrastructure.configurations;

import com.kaua.ecommerce.infrastructure.configurations.properties.aws.AwsCloudProperties;
import com.kaua.ecommerce.infrastructure.configurations.properties.aws.AwsS3StorageProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@Profile({"development", "production"})
public class AwsConfig {

    @Bean
    @ConfigurationProperties("aws-cloud")
    public AwsCloudProperties awsCloudProperties() {
        return new AwsCloudProperties();
    }

    @Bean
    @ConfigurationProperties("aws-cloud.s3")
    public AwsS3StorageProperties awsS3StorageProperties() {
        return new AwsS3StorageProperties();
    }

    @Bean
    public AwsBasicCredentials awsBasicCredentials(final AwsCloudProperties awsCloudProperties) {
        return AwsBasicCredentials
                .create(awsCloudProperties.getAccessKey(), awsCloudProperties.getSecretKey());
    }

    @Bean
    public S3Client s3Client(
            final AwsBasicCredentials awsBasicCredentials,
            final AwsS3StorageProperties awsS3StorageProperties
    ) {
        return S3Client.builder()
                .region(Region.of(awsS3StorageProperties.getRegion()))
                .credentialsProvider(() -> awsBasicCredentials)
                .build();
    }
}
