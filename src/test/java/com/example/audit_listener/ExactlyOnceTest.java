package com.example.audit_listener;

import com.example.audit_listener.config.TestConfig;
import com.example.audit_listener.dto.KafkaMethodLog;
import com.example.audit_listener.kafka.KafkaConsumer;
import com.example.audit_listener.repositories.MethodRepository;
import com.example.audit_listener.services.MethodService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

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
public class ExactlyOnceTest extends AbstractContainer {

    private final int sleepTime = 3000;

    private final String topic = System.getProperty("audit-listener.kafka.method-topic-name");

    @Autowired
    private KafkaConsumer kafkaConsumer;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private MethodService service;

    @Autowired
    private MethodRepository repository;

    /**
     * Imitates situation where consumer received the same message twice.
     * First, message is written to database.
     * Then exception will be thrown because such message already saved in database.
     *
     * @throws InterruptedException
     */
    @Test
    public void testSuccessThenFailure() throws InterruptedException, JsonProcessingException {
        Logger logger = Mockito.mock(Logger.class);
        String log = serialize(TestConfig.getAnnotationLog());
        kafkaConsumer.setMethodService(service);

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

        long count = repository.count();

        Thread.sleep(sleepTime);
        kafkaTemplate.send(topic, log);
        latchException.await(1, TimeUnit.SECONDS);

        Assertions.assertEquals(count, repository.count());
    }

    /**
     * Imitates situation where consumer cant process message and receives it several times.
     *
     * @throws InterruptedException
     */
    @Test
    public void testFailureThenSuccess() throws InterruptedException, JsonProcessingException {
        MethodService service = Mockito.mock(MethodService.class);
        kafkaConsumer.setMethodService(service);

        CountDownLatch latch = new CountDownLatch(2);
        String log = serialize(TestConfig.getAnnotationLog());

        Mockito.when(service.save(any(KafkaMethodLog.class))).thenAnswer(answer -> {
            latch.countDown();
            throw new DataAccessException("") {};
        });

        Thread.sleep(sleepTime);
        kafkaTemplate.send(topic, log);
        boolean isRetried = latch.await(10, TimeUnit.SECONDS);
        Assertions.assertTrue(isRetried);
    }

}
