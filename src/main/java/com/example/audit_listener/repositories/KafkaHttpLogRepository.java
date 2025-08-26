package com.example.audit_listener.repositories;

import com.example.audit_listener.dto.KafkaHttpLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface KafkaHttpLogRepository extends JpaRepository<KafkaHttpLog, Long> {

    Optional<KafkaHttpLog> findByDateAndUrlAndRequest(LocalDateTime date, String url, String request);

}
