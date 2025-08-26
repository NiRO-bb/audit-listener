package com.example.audit_listener;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.ConfluentKafkaContainer;

public class ContextSetup implements BeforeAllCallback {

    public PostgreSQLContainer<?> containerSQL = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    public ConfluentKafkaContainer containerKafka = new ConfluentKafkaContainer("confluentinc/cp-kafka:7.4.0");

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        containerSQL.start();
        containerKafka.start();
        updateProperties();
    }

    private void updateProperties() {
        System.setProperty("spring.datasource.url", containerSQL.getJdbcUrl());
        System.setProperty("spring.datasource.username", containerSQL.getUsername());
        System.setProperty("spring.datasource.password", containerSQL.getPassword());
        System.setProperty("spring.kafka.bootstrap-servers", containerKafka.getBootstrapServers());
        System.setProperty("audit-listener.kafka.consumer.group-id", "test_group");
        System.setProperty("audit-listener.kafka.topic-name", "test_topic");
    }

}
