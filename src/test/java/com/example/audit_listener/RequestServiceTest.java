package com.example.audit_listener;

import com.example.audit_listener.config.TestConfig;
import com.example.audit_listener.dto.KafkaRequestLog;
import com.example.audit_listener.exceptions.AlreadyExistsException;
import com.example.audit_listener.repositories.RequestRepository;
import com.example.audit_listener.services.RequestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {
        "listeners=PLAINTEXT://localhost:9094", "port=9094"
})
public class RequestServiceTest extends AbstractContainer {

    @Autowired
    public RequestService service;

    @Autowired
    public RequestRepository repository;

    @Test
    public void testSaveSuccess() {
        KafkaRequestLog log = TestConfig.getHttpLog();
        KafkaRequestLog savedLog = service.save(log);
        Assertions.assertTrue(repository.existsById(savedLog.getId()));
    }

    @Test
    public void testSaveFailure() {
        KafkaRequestLog log = TestConfig.getHttpLog();
        service.save(log);
        Assertions.assertThrows(AlreadyExistsException.class, () -> service.save(log));
    }

}
