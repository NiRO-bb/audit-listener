package com.example.audit_listener;

import com.example.audit_listener.config.TestConfig;
import com.example.audit_listener.dto.KafkaMethodLog;
import com.example.audit_listener.exceptions.AlreadyExistsException;
import com.example.audit_listener.repositories.MethodRepository;
import com.example.audit_listener.services.MethodService;
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
public class MethodServiceTest extends AbstractContainer {

    @Autowired
    public MethodService service;

    @Autowired
    public MethodRepository repository;

    @Test
    public void testSaveSuccess() {
        KafkaMethodLog log = TestConfig.getAnnotationLog();
        KafkaMethodLog savedLog = service.save(log);
        Assertions.assertTrue(repository.existsById(savedLog.getLogId()));
    }

    @Test
    public void testSaveFailure() {
        KafkaMethodLog log = TestConfig.getAnnotationLog();
        service.save(log);
        Assertions.assertThrows(AlreadyExistsException.class, () -> service.save(log));
    }

}
