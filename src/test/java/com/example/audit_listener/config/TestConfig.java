package com.example.audit_listener.config;

import com.example.audit_listener.dto.KafkaMethodLog;
import com.example.audit_listener.dto.KafkaRequestLog;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.UUID;

@Configuration
public class TestConfig {

    public static KafkaMethodLog getAnnotationLog() {
        return new KafkaMethodLog(
                LocalDateTime.now(),
                "INFO",
                "START",
                UUID.randomUUID(),
                "Class.method",
                "{ \"field\":\"value\" }"
        );
    }

    public static KafkaRequestLog getHttpLog() {
        return new KafkaRequestLog(
                LocalDateTime.now(),
                "Outgoing",
                "GET",
                200,
                "http://localhost:8081/get",
                "{}",
                "{ \"field\":\"value\" }"
        );
    }
  
}
