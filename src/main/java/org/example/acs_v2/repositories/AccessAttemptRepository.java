package org.example.acs_v2.repositories;

import org.example.acs_v2.models.AccessAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessAttemptRepository extends JpaRepository<AccessAttempt, Long> {
    List<AccessAttempt> findBySuccess(boolean success);
    List<AccessAttempt> findAllByOrderByTimestampDesc();
    List<AccessAttempt> findBySuccessOrderByTimestampDesc(boolean success);
    List<AccessAttempt> findByUserIdOrderByTimestampDesc(Long userId);
    List<AccessAttempt> findByUserIdAndSuccessOrderByTimestampDesc(Long userId, boolean success);
}

