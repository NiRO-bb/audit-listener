package com.example.audit_listener;

import com.example.audit_listener.config.TestConfig;
import com.example.audit_listener.dto.KafkaMethodLog;
import com.example.audit_listener.kafka.KafkaConsumer;
import com.example.audit_listener.services.MethodService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.example.audit_listener.util.JsonUtil.serialize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {
        "listeners=PLAINTEXT://localhost:9094", "port=9094"
})
public class KafkaConsumerTest extends AbstractContainer {

    private final String topic = System.getProperty("audit-listener.kafka.method-topic-name");

    private final int sleepTime = 3000;

    @Autowired
    private KafkaConsumer consumer;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockitoBean
    private MethodService service;

    @Test
    public void testHandleStringSuccess() throws InterruptedException, JsonProcessingException {
        CountDownLatch latch = new CountDownLatch(1);
        Mockito.when(service.save(any(KafkaMethodLog.class))).thenAnswer(answer -> {
            latch.countDown();
            return null;
        });

        Thread.sleep(sleepTime);
        kafkaTemplate.send(topic, serialize(TestConfig.getAnnotationLog()));
        latch.await(1, TimeUnit.SECONDS);
        Mockito.verify(service).save(any(KafkaMethodLog.class));
    }

    @Test
    public void testHandleStringFailure() throws InterruptedException, JsonProcessingException {
        Mockito.when(service.save(any(KafkaMethodLog.class))).thenThrow(new DataAccessException("error") {});
        Thread.sleep(sleepTime);
        kafkaTemplate.send(topic, serialize(TestConfig.getAnnotationLog()));
        Mockito.verify(service, Mockito.never()).save(any(KafkaMethodLog.class));
    }

    @Test
    public void testHandleSerializeException() throws InterruptedException {
        Logger logger = Mockito.mock(Logger.class);
        consumer.setLogger(logger);

        Thread.sleep(sleepTime);
        kafkaTemplate.send(topic, "error");
        Thread.sleep(1000);
        Mockito.verify(logger).error(anyString(), any(Object.class));
    }

}
