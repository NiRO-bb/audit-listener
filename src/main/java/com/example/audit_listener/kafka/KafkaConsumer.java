package com.example.audit_listener.kafka;

import com.example.audit_listener.dto.KafkaMethodLog;
import com.example.audit_listener.dto.KafkaRequestLog;
import com.example.audit_listener.exceptions.AlreadyExistsException;
import com.example.audit_listener.services.MethodService;
import com.example.audit_listener.services.RequestService;
import com.example.audit_listener.util.JsonUtil;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Received messages from Kafka topic and manages their processing.
 */
@Component
@KafkaListener(id="${audit-listener.kafka.consumer.group-id}",
        topics={"${audit-listener.kafka.method-topic-name}",
                "${audit-listener.kafka.request-topic-name}"})
public class KafkaConsumer {

    @Autowired
    @Setter
    private MethodService methodService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Setter
    private Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @Value("${audit-listener.kafka.method-topic-name}")
    private String methodTopic;

    @Value("${audit-listener.kafka.request-topic-name}")
    private String requestTopic;

    @Value("${audit-listener.kafka.error-topic-name}")
    private String errorTopic;

    @RetryableTopic(
            backoff = @Backoff(delay = 1000),
            attempts = "3",
            autoCreateTopics = "false",
            include = { DataAccessException.class }
    )
    @KafkaHandler
    public void handleString(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, Acknowledgment acknowledgment) {
        try {
            if (topic.equals(requestTopic)) {
                handleRequest((KafkaRequestLog) JsonUtil.deserialize(message, KafkaRequestLog.class));
            } else if (topic.equals(methodTopic)) {
                handleMethod((KafkaMethodLog) JsonUtil.deserialize(message, KafkaMethodLog.class));
            }
            acknowledgment.acknowledge();
        } catch (IOException exception) {
            logger.error("Message not received (IOException) - {}", exception.getMessage());
            kafkaTemplate.send(errorTopic, exception.getMessage());
            acknowledgment.acknowledge();
        } catch (AlreadyExistsException exception) {
            logger.error("Message already received (exactly-once) - {}", exception.getMessage());
            acknowledgment.acknowledge();
        } catch (DataAccessException exception) {
            logger.error("Message not received (DataAccessException) - {}", exception.getMessage());
            throw exception;
        }
    }

    /**
     * Handles KafkaMethodLog message type.
     * Notifies about received message and saves it.
     *
     * @param log received message
     */
    private void handleMethod(KafkaMethodLog log) {
        methodService.save(log);
        logger.info("Method log received - {}", log.desc());
    }

    /**
     * Handles KafkaRequestLog message type.
     * Notifies about received message and saves it.
     *
     * @param log received message
     */
    private void handleRequest(KafkaRequestLog log) {
        requestService.save(log);
        logger.info("Request log received - {}", log.desc());
    }

}
