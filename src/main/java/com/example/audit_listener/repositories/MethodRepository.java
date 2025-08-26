package com.example.audit_listener.repositories;

import com.example.audit_listener.dto.KafkaMethodLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MethodRepository extends ElasticsearchRepository<KafkaMethodLog, String> {

    Optional<KafkaMethodLog> findByStageAndId(String stage, String id);

}
