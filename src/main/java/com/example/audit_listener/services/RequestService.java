package com.example.audit_listener.services;

import com.example.audit_listener.dto.KafkaRequestLog;
import com.example.audit_listener.exceptions.AlreadyExistsException;
import com.example.audit_listener.repositories.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Provides means for saving MethodLog as document in elasticsearch index.
 */
@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository repository;

    /**
     * Saves passed log as document.
     *
     * @param log log must be saved
     * @return saved log
     */
    public KafkaRequestLog save(KafkaRequestLog log) {
        if (repository.findByDateAndUrl(log.getDate(), log.getUrl()).isPresent()) {
            throw new AlreadyExistsException("This log has been already received");
        }
        return repository.save(log);
    }

}
