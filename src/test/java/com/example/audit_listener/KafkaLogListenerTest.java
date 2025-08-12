package com.example.audit_listener;

import com.example.audit_listener.config.TestConfig;
import com.example.audit_listener.dto.KafkaAnnotationLog;
import com.example.audit_listener.dto.KafkaHttpLog;
import com.example.audit_listener.services.LogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ExtendWith(ContextSetup.class)
@Import(TestConfig.class)
public class KafkaLogListenerTest {

    private final String topic = System.getProperty("audit-listener.kafka.topic-name");

    private final int sleepTime = 3000;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockitoBean
    private LogService service;

    @Test
    public void testHandleAnnotationSuccess() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Mockito.when(service.saveAnnotationLog(any(KafkaAnnotationLog.class))).then(answer -> {
            latch.countDown();
            return null;
        });

        KafkaAnnotationLog log = TestConfig.getAnnotationLog();
        Thread.sleep(sleepTime);
        kafkaTemplate.send(topic, log);
        latch.await(1, TimeUnit.SECONDS);
        Mockito.verify(service).saveAnnotationLog(any(KafkaAnnotationLog.class));
    }

    @Test
    public void testHandleAnnotationFailure() throws InterruptedException {
        Mockito.when(service.saveAnnotationLog(any(KafkaAnnotationLog.class))).thenThrow(new DataAccessException("error") {});
        Thread.sleep(sleepTime);
        kafkaTemplate.send(topic, new KafkaAnnotationLog());
        Mockito.verify(service, Mockito.never()).saveAnnotationLog(any(KafkaAnnotationLog.class));
    }

    @Test
    public void testHandleHttpSuccess() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Mockito.when(service.saveHttpLog(any(KafkaHttpLog.class))).then(answer -> {
            latch.countDown();
            return null;
        });

        KafkaHttpLog log = TestConfig.getHttpLog();
        Thread.sleep(sleepTime);
        kafkaTemplate.send(topic, log);
        latch.await(1, TimeUnit.SECONDS);
        Mockito.verify(service).saveHttpLog(any(KafkaHttpLog.class));
    }

    @Test
    public void testHandleHttpFailure() throws InterruptedException {
        Thread.sleep(sleepTime);
        Mockito.when(service.saveHttpLog(any(KafkaHttpLog.class))).thenThrow(new DataAccessException("error") {});
        kafkaTemplate.send(topic, new KafkaHttpLog());
        Mockito.verify(service, Mockito.never()).saveHttpLog(any(KafkaHttpLog.class));
    }

    @Test
    public void testHandleUnknownSuccess() throws InterruptedException {
        Thread.sleep(sleepTime);
        kafkaTemplate.send(topic, "test");
        Mockito.verify(service, Mockito.never()).saveHttpLog(any(KafkaHttpLog.class));
        Mockito.verify(service, Mockito.never()).saveAnnotationLog(any(KafkaAnnotationLog.class));
    }

}
