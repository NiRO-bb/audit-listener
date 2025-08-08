package com.example.audit_listener.services;

import com.example.audit_listener.dto.KafkaAnnotationLog;
import com.example.audit_listener.dto.KafkaHttpLog;
import com.example.audit_listener.repositories.KafkaAnnotationLogRepository;
import com.example.audit_listener.repositories.KafkaHttpLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

/**
 * Provides means for saving received logs in database.
 */
@Service
@RequiredArgsConstructor
public class LogService {

    private final KafkaHttpLogRepository httpRepository;

    private final KafkaAnnotationLogRepository annotationRepository;

    /**
     * Saves KafkaAnnotationLog in database.
     *
     * @param log log must be saved
     * @return saved log
     */
    public KafkaAnnotationLog saveAnnotationLog(KafkaAnnotationLog log) {
        if (annotationRepository.findByIdAndStage(log.getId(), log.getStage()).isPresent()) {
            throw new DataAccessException("This log has been already received") {};
        }
        return annotationRepository.save(log);
    }

    /**
     * Saves KafkaHttpLog in database.
     *
     * @param log log must be saved
     * @return saved log
     */
    public KafkaHttpLog saveHttpLog(KafkaHttpLog log) {
        if (httpRepository.findByDateAndUrlAndRequest(log.getDate(), log.getUrl(), log.getRequest())
                .isPresent()) {
            throw new DataAccessException("This log has been already received") {};
        }
        return httpRepository.save(log);
    }

}
