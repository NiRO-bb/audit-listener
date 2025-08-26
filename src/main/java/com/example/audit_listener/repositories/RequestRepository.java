package com.example.audit_listener.repositories;

import com.example.audit_listener.dto.KafkaRequestLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RequestRepository extends ElasticsearchRepository<KafkaRequestLog, String> {

    Optional<KafkaRequestLog> findByDateAndUrl(LocalDateTime date, String url);

}
