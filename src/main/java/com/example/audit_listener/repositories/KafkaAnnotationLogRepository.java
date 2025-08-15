package com.example.audit_listener.repositories;

import com.example.audit_listener.dto.KafkaAnnotationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface KafkaAnnotationLogRepository extends JpaRepository<KafkaAnnotationLog, Long> {

    Optional<KafkaAnnotationLog> findByIdAndStage(UUID id, String stage);

    long countById(UUID id);

}
