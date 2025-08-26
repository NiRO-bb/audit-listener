package com.example.audit_listener.services;

import com.example.audit_listener.dto.KafkaAnnotationLog;
import com.example.audit_listener.dto.KafkaHttpLog;
import com.example.audit_listener.exceptions.AlreadyExistsException;
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
        try {
            if (annotationRepository.findByIdAndStage(log.getId(), log.getStage()).isPresent()) {
                throw new AlreadyExistsException("This log has been already received");
            }
            return annotationRepository.save(log);
        } catch (DataAccessException exception) {
            throw new DataAccessException(exception.getMessage()) {};
        }
    }

    /**
     * Saves KafkaHttpLog in database.
     *
     * @param log log must be saved
     * @return saved log
     */
    public KafkaHttpLog saveHttpLog(KafkaHttpLog log) {
        try {
            if (httpRepository.findByDateAndUrlAndRequest(log.getDate(), log.getUrl(), log.getRequest()).isPresent()) {
                throw new AlreadyExistsException("This log has been already received");
            }
            return httpRepository.save(log);
        } catch (DataAccessException exception) {
            throw new DataAccessException(exception.getMessage()) {};
        }
    }

}
