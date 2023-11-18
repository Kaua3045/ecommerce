package com.kaua.ecommerce.infrastructure.configurations.properties.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class KafkaTopicProperty implements InitializingBean {

    private final Logger log = LoggerFactory.getLogger(KafkaTopicProperty.class);

    private String topicName;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.debug(toString());
    }

    @Override
    public String toString() {
        return "KafkaTopicProperty{" +
                "topicName='" + topicName + '\'' +
                '}';
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
