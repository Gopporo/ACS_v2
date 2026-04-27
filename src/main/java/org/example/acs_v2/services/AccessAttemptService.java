package org.example.acs_v2.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.models.AccessAttempt;
import org.example.acs_v2.repositories.AccessAttemptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccessAttemptService {

    private final AccessAttemptRepository accessAttemptRepository;

    @Transactional(readOnly = true)
    public List<AccessAttempt> getAllAttempts() {
        return accessAttemptRepository.findAllByOrderByTimestampDesc();
    }

    @Transactional(readOnly = true)
    public List<AccessAttempt> getAttemptsBySuccess(boolean success) {
        return accessAttemptRepository.findBySuccessOrderByTimestampDesc(success);
    }

    @Transactional(readOnly = true)
    public List<AccessAttempt> getAttemptsByUser(Long userId) {
        return accessAttemptRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<AccessAttempt> getAttemptsByUserAndSuccess(Long userId, boolean success) {
        return accessAttemptRepository.findByUserIdAndSuccessOrderByTimestampDesc(userId, success);
    }

    @Transactional(readOnly = true)
    public List<AccessAttempt> findBySuccess(boolean success) {
        return accessAttemptRepository.findBySuccess(success);
    }

    @Transactional(readOnly = true)
    public List<AccessAttempt> findAll() {
        return accessAttemptRepository.findAll();
    }
}

