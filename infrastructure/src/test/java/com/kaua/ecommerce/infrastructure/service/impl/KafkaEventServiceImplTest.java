package com.kaua.ecommerce.infrastructure.service.impl;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

@Tag("unitTest")
public class KafkaEventServiceImplTest {

    @Test
    void testSendMethodException() {
        KafkaTemplate<String, Object> kafkaTemplateMock = Mockito.mock(KafkaTemplate.class);
        KafkaEventServiceImpl kafkaEventService = new KafkaEventServiceImpl(kafkaTemplateMock);

        CompletableFuture<SendResult<String, Object>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Simulated exception"));

        Mockito.when(kafkaTemplateMock.send(Mockito.anyString(), Mockito.any())).thenReturn(failedFuture);

        kafkaEventService.send("testMessage", "testTopic");

        Mockito.verify(kafkaTemplateMock).send("testTopic", "testMessage");
    }

}
