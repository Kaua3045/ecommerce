package com.kaua.ecommerce.infrastructure.configurations.properties.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class StorageProperties implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(StorageProperties.class);

    private String providerUrl;

    public StorageProperties() {}

    @Override
    public void afterPropertiesSet() throws Exception {
        log.debug(toString());
    }

    @Override
    public String toString() {
        return "StorageProperties{" +
                "providerUrl='" + providerUrl + '\'' +
                '}';
    }

    public String getProviderUrl() {
        return providerUrl;
    }

    public void setProviderUrl(String providerUrl) {
        this.providerUrl = providerUrl;
    }
}
