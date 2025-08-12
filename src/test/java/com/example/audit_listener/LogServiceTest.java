package com.example.audit_listener;

import com.example.audit_listener.config.TestConfig;
import com.example.audit_listener.dto.KafkaAnnotationLog;
import com.example.audit_listener.dto.KafkaHttpLog;
import com.example.audit_listener.services.LogService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;

@SpringBootTest
@ExtendWith(ContextSetup.class)
public class LogServiceTest {

    @Autowired
    private LogService service;

    @Test
    public void testSaveAnnotationSuccess() {
        KafkaAnnotationLog log = TestConfig.getAnnotationLog();
        KafkaAnnotationLog savedLog = service.saveAnnotationLog(log);
        Assertions.assertNotNull(savedLog);
    }

    @Test
    public void testSaveAnnotationFailure() {
        Assertions.assertThrows(DataAccessException.class,
                () -> service.saveAnnotationLog(new KafkaAnnotationLog()));
    }

    @Test
    public void testSaveHttpSuccess() {
        KafkaHttpLog log = TestConfig.getHttpLog();
        KafkaHttpLog savedLog = service.saveHttpLog(log);
        Assertions.assertNotNull(savedLog);
    }

    @Test
    public void testSaveHttpFailure() {
        Assertions.assertThrows(DataAccessException.class,
                () -> service.saveHttpLog(new KafkaHttpLog()));
    }

}
