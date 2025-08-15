package com.example.audit_listener.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents received (from Kafka topic) log structure for JsonDeserializer.
 * Also serves as DTO for database.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "annotation_logs")
public class KafkaAnnotationLog {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long logId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime date;

    @Column(name = "logging_level")
    private String logLevel;

    /**
     * Represents stage of method execution: START, END or ERROR.
     */
    private String stage;

    @Column(name = "cross_cutting_id")
    private UUID id;

    @Column(name = "method_name")
    private String methodName;

    private String body;

    public KafkaAnnotationLog(LocalDateTime date, String logLevel, String stage, UUID id, String methodName, String body) {
        this.date = date;
        this.logLevel = logLevel;
        this.stage = stage;
        this.id = id;
        this.methodName = methodName;
        this.body = body;
    }

    /**
     * Provides short description of class instance for logger.
     * Uses date, methodName and body fields.
     *
     * @return short class instance description
     */
    public String desc() {
        return String.format("timestamp:%s, method:%s, body:%s", date, methodName, body);
    }

}
