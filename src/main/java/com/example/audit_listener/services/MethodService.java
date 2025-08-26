package com.example.audit_listener.services;

import com.example.audit_listener.dto.KafkaMethodLog;
import com.example.audit_listener.exceptions.AlreadyExistsException;
import com.example.audit_listener.repositories.MethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Provides means for saving RequestLog as document in elasticsearch index.
 */
@Service
@RequiredArgsConstructor
public class MethodService {

    private final MethodRepository repository;

    /**
     * Saves passed log as document.
     *
     * @param log log must be saved
     * @return saved log
     */
    public KafkaMethodLog save(KafkaMethodLog log) {
        if (repository.findByStageAndId(log.getStage(), log.getId().toString()).isPresent()) {
            throw new AlreadyExistsException("This log has been already received");
        }
        return repository.save(log);
    }

}
