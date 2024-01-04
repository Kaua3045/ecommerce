package com.kaua.ecommerce.infrastructure.configurations.properties.aws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class AwsCloudProperties implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(AwsCloudProperties.class);

    private String accessKey;
    private String secretKey;

    public AwsCloudProperties() {}

    @Override
    public void afterPropertiesSet() throws Exception {
        log.debug("properties set");
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
