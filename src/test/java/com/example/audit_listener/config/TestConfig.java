package com.example.audit_listener.config;

import com.example.audit_listener.dto.KafkaAnnotationLog;
import com.example.audit_listener.dto.KafkaHttpLog;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
public class TestConfig {

    public static KafkaAnnotationLog getAnnotationLog() {
        return new KafkaAnnotationLog(
                LocalDateTime.now(),
                "INFO",
                "START",
                UUID.randomUUID(),
                "Class.method",
                "{ \"field\":\"value\" }"
        );
    }

    public static KafkaHttpLog getHttpLog() {
        return new KafkaHttpLog(
                LocalDateTime.now(),
                "Outgoing",
                "GET",
                200,
                "http://localhost:8081/get",
                "{}",
                "{ \"field\":\"value\" }"
        );
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getProperty("spring.kafka.bootstrap-servers"));
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configs.put(JsonSerializer.TYPE_MAPPINGS,
                "http:com.example.audit_listener.dto.KafkaHttpLog, " +
                        "annotation:com.example.audit_listener.dto.KafkaAnnotationLog");
        ProducerFactory<String, Object> factory = new DefaultKafkaProducerFactory<>(configs);
        return new KafkaTemplate<>(factory);
    }

}
