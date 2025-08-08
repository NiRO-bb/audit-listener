package com.example.audit_listener;

import com.example.audit_listener.dto.KafkaAnnotationLog;
import com.example.audit_listener.dto.KafkaHttpLog;
import com.example.audit_listener.services.LogService;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ExtendWith(ContextSetup.class)
public class KafkaLogListenerTest {

    public static KafkaTemplate<String, Object> kafkaTemplate;

    private final int sleepTime = 3000;

    @MockitoBean
    private LogService service;

    @BeforeAll
    public static void setup() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getProperty("spring.kafka.bootstrap-servers"));
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configs.put(JsonSerializer.TYPE_MAPPINGS,
                "http:com.example.audit_listener.dto.KafkaHttpLog, " +
                        "annotation:com.example.audit_listener.dto.KafkaAnnotationLog");
        ProducerFactory<String, Object> factory = new DefaultKafkaProducerFactory<>(configs);
        kafkaTemplate = new KafkaTemplate<>(factory);
    }

    @Test
    public void testHandleAnnotationSuccess() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Mockito.when(service.saveAnnotationLog(any(KafkaAnnotationLog.class))).then(answer -> {
            latch.countDown();
            return null;
        });

        KafkaAnnotationLog log = new KafkaAnnotationLog(
                LocalDateTime.now(),
                "INFO",
                "START",
                UUID.randomUUID(),
                "Class.method",
                "{ \"field\":\"value\" }"
        );
        Thread.sleep(sleepTime);
        kafkaTemplate.send(System.getProperty("audit-listener.kafka.topic-name"), log);
        latch.await(1, TimeUnit.SECONDS);
        Mockito.verify(service).saveAnnotationLog(any(KafkaAnnotationLog.class));
    }

    @Test
    public void testHandleAnnotationFailure() throws InterruptedException {
        Mockito.when(service.saveAnnotationLog(any(KafkaAnnotationLog.class))).thenThrow(new DataAccessException("error") {});
        Thread.sleep(sleepTime);
        kafkaTemplate.send(System.getProperty("audit-listener.kafka.topic-name"), new KafkaAnnotationLog());
        Mockito.verify(service, Mockito.never()).saveAnnotationLog(any(KafkaAnnotationLog.class));
    }

    @Test
    public void testHandleHttpSuccess() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Mockito.when(service.saveHttpLog(any(KafkaHttpLog.class))).then(answer -> {
            latch.countDown();
            return null;
        });

        KafkaHttpLog log = new KafkaHttpLog(
                LocalDateTime.now(),
                "Outgoing",
                "GET",
                200,
                "http://localhost:8081/get",
                "{}",
                "{ \"field\":\"value\" }"
        );
        Thread.sleep(sleepTime);
        kafkaTemplate.send(System.getProperty("audit-listener.kafka.topic-name"), log);
        latch.await(1, TimeUnit.SECONDS);
        Mockito.verify(service).saveHttpLog(any(KafkaHttpLog.class));
    }

    @Test
    public void testHandleHttpFailure() throws InterruptedException {
        Thread.sleep(sleepTime);
        Mockito.when(service.saveHttpLog(any(KafkaHttpLog.class))).thenThrow(new DataAccessException("error") {});
        kafkaTemplate.send(System.getProperty("audit-listener.kafka.topic-name"), new KafkaHttpLog());
        Mockito.verify(service, Mockito.never()).saveHttpLog(any(KafkaHttpLog.class));
    }

    @Test
    public void testHandleUnknownSuccess() throws InterruptedException {
        Thread.sleep(sleepTime);
        kafkaTemplate.send(System.getProperty("audit-listener.kafka.topic-name"), "test");
        Mockito.verify(service, Mockito.never()).saveHttpLog(any(KafkaHttpLog.class));
        Mockito.verify(service, Mockito.never()).saveAnnotationLog(any(KafkaAnnotationLog.class));
    }

}
