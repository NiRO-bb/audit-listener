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

/**
 * Represents received (from Kafka topic) log structure for JsonDeserializer.
 * Also serves as DTO for database.
 */
@Getter
@Setter
@NoArgsConstructor
@Document(createIndex = true, indexName = "#{@getRequestIndex}")
public class KafkaRequestLog {

    @JsonIgnore
    @Id
    private String id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @Field(type = FieldType.Date, format = DateFormat.strict_date_hour_minute_second_millis)
    private LocalDateTime date;

    @Field(name = "direction")
    private String type;

    private String method;

    private int statusCode;

    private String url;

    @Field(name = "requestBody")
    private String request;

    @Field(name = "responseBody")
    private String response;

    public KafkaRequestLog(LocalDateTime date, String type, String method, int statusCode, String url, String request, String response) {
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
