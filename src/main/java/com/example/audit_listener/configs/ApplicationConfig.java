package com.example.audit_listener.configs;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines based settings of application.
 * Provides some beans for application.
 */
@Configuration
@EnableKafka
public class ApplicationConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value("${audit-listener.kafka.consumer.group-id}")
    private String groupId;

    /**
     * Creates ConsumerFactory instance.
     * Sets some settings: StringDeserializer for key,
     * JsonDeserializer for value, disable offset auto commit.
     * Uses JsonDeserializer type mapping:
     * 'http' for KafkaHttpLog class, 'annotation' for KafkaAnnotationLog class.
     *
     * @return
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        configs.put(JsonDeserializer.TYPE_MAPPINGS,
                "http:com.example.audit_listener.dto.KafkaHttpLog, " +
                        "annotation:com.example.audit_listener.dto.KafkaAnnotationLog");
        configs.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.audit_listener.dto");
        return new DefaultKafkaConsumerFactory<>(configs);
    }

    /**
     * Creates listener container factory used for manage kafka listeners.
     * Uses ConsumerFactory bean.
     * Sets ack mode to manual.
     *
     * @return
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

}
