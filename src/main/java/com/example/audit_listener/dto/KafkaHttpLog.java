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

/**
 * Represents received (from Kafka topic) log structure for JsonDeserializer.
 * Also serves as DTO for database.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "http_logs")
public class KafkaHttpLog {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime date;

    private String type;

    private String method;

    @Column(name = "status_code")
    private int statusCode;

    private String url;

    private String request;

    private String response;

    public KafkaHttpLog(LocalDateTime date, String type, String method, int statusCode, String url, String request, String response) {
        this.date = date;
        this.type = type;
        this.method = method;
        this.statusCode = statusCode;
        this.url = url;
        this.request = request;
        this.response = response;
    }

    /**
     * Provides short description of class instance for logger.
     * Uses date, url and statusCode fields.
     *
     * @return short class instance description
     */
    public String desc() {
        return String.format("timestamp:%s, url:%s, statusCode:%d", date, url, statusCode);
    }

}
