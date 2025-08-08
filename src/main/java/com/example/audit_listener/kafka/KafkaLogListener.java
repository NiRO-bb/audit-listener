package com.example.audit_listener.kafka;

import com.example.audit_listener.dto.KafkaAnnotationLog;
import com.example.audit_listener.dto.KafkaHttpLog;
import com.example.audit_listener.services.LogService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Received messages from Kafka topic and manages their processing.
 */
@Component
@KafkaListener(id="${audit-listener.kafka.consumer.group-id}",
        topics="${audit-listener.kafka.topic-name}")
@RequiredArgsConstructor
public class KafkaLogListener {

    private final Logger logger = LoggerFactory.getLogger(KafkaLogListener.class);

    private final LogService service;

    /**
     * Handles KafkaAnnotationLog message type.
     * Notifies about received message and saves it.
     *
     * @param log received message
     */
    @KafkaHandler
    public void handleAnnotation(KafkaAnnotationLog log, Acknowledgment acknowledgment) {
        try {
            service.saveAnnotationLog(log);
            logger.info("Annotation log received - {}", log.desc());
            acknowledgment.acknowledge();
        } catch (DataAccessException exception) {
            logger.error("Log receiving was failed - {}", exception.getMessage());
        }
    }

    /**
     * Handles KafkaHttpLog message type.
     * Notifies about received message and saves it.
     *
     * @param log received message
     */
    @KafkaHandler
    public void handleHttp(KafkaHttpLog log, Acknowledgment acknowledgment) {
        try {
            service.saveHttpLog(log);
            logger.info("Http log received - {}", log.desc());
            acknowledgment.acknowledge();
        } catch (DataAccessException exception) {
            logger.error("Log receiving was failed - {}", exception.getMessage());
        }
    }

    /**
     * Handles unknown message type.
     * Notifies about message.
     *
     * @param message received message
     */
    @KafkaHandler(isDefault = true)
    public void handleUnknown(String message) {
        logger.warn("Unknown log received - {}", message);
    }

}
