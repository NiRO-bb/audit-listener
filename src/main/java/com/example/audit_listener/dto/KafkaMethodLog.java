package com.example.audit_listener.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents received (from Kafka topic) log structure for JsonDeserializer.
 * Also serves as DTO for database.
 */
@Getter
@Setter
@NoArgsConstructor
@Document(createIndex = true, indexName = "#{@getMethodIndex}")
public class KafkaMethodLog {

    @JsonIgnore
    @Id
    @Field(name = "id")
    private String logId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @Field(type = FieldType.Date, format = DateFormat.strict_date_hour_minute_second_millis)
    private LocalDateTime date;

    private String logLevel;

    private String stage;

    @Field(name = "cross_cutting_id")
    private UUID id;

    @Field(name = "method")
    private String methodName;

    private String body;

    public KafkaMethodLog(LocalDateTime date, String logLevel, String stage, UUID id, String methodName, String body) {
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
