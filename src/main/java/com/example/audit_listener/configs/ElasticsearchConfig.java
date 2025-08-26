package com.example.audit_listener.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sets names of used indices of Elasticsearch.
 */
@Configuration
public class ElasticsearchConfig {

    @Value("${audit-listener.index.method}")
    private String methodIndex;

    @Value("${audit-listener.index.request}")
    private String requestIndex;

    /**
     * Creates bean used as index name for KafkaMethodLog document.
     *
     * @return index name
     */
    @Bean
    public String getMethodIndex() {
        return methodIndex;
    }

    /**
     * Creates bean used as index name for KafkaRequestLog document.
     *
     * @return index name
     */
    @Bean
    public String getRequestIndex() {
        return requestIndex;
    }

}
