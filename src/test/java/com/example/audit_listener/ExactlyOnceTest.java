package com.example.audit_listener;

import com.example.audit_listener.config.TestConfig;
import com.example.audit_listener.dto.KafkaAnnotationLog;
import com.example.audit_listener.kafka.KafkaLogListener;
import com.example.audit_listener.repositories.KafkaAnnotationLogRepository;
import com.example.audit_listener.services.LogService;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
@ExtendWith(ContextSetup.class)
@Import(TestConfig.class)
public class ExactlyOnceTest {

    private final int sleepTime = 3000;

    private final String topic = System.getProperty("audit-listener.kafka.topic-name");

    @Autowired
    private KafkaLogListener kafkaLogListener;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private LogService service;

    @Autowired
    private KafkaAnnotationLogRepository repository;

    /**
     * Imitates situation where consumer received the same message twice.
     * First, message is written to database.
     * Then exception will be thrown because such message already saved in database.
     *
     * @throws InterruptedException
     */
    @Test
    public void testSuccessThenFailure() throws InterruptedException {
        Logger logger = Mockito.mock(Logger.class);
        KafkaAnnotationLog log = TestConfig.getAnnotationLog();
        kafkaLogListener.setService(service);

        CountDownLatch latchSuccess = new CountDownLatch(1);
        CountDownLatch latchException = new CountDownLatch(1);

        Mockito.doAnswer(invocation -> {
            latchSuccess.countDown();
            return null;
        }).when(logger).info(anyString(), any(Object.class));
        Mockito.doAnswer(invocation -> {
            latchException.countDown();
            return null;
        }).when(logger).error(anyString(), any(Object.class));

        Thread.sleep(sleepTime);
        kafkaTemplate.send(topic, log);
        latchSuccess.await(1, TimeUnit.SECONDS);

        long count = repository.countById(log.getId());
        Assertions.assertEquals(1, count);

        Thread.sleep(sleepTime);
        kafkaTemplate.send(topic, log);
        latchException.await(1, TimeUnit.SECONDS);

        count = repository.countById(log.getId());
        Assertions.assertEquals(1, count);
    }

    /**
     * Imitates situation where consumer cant process message and receives it several times.
     *
     * @throws InterruptedException
     */
    @Test
    public void testFailureThenSuccess() throws InterruptedException {
        LogService service = Mockito.mock(LogService.class);
        kafkaLogListener.setService(service);

        CountDownLatch latch = new CountDownLatch(2);
        KafkaAnnotationLog log = TestConfig.getAnnotationLog();

        Mockito.when(service.saveAnnotationLog(any(KafkaAnnotationLog.class))).thenAnswer(answer -> {
            latch.countDown();
            throw new DataAccessException("") {};
        });

        Thread.sleep(sleepTime);
        kafkaTemplate.send(topic, log);
        boolean isRetried = latch.await(10, TimeUnit.SECONDS);
        Assertions.assertTrue(isRetried);
    }

}
