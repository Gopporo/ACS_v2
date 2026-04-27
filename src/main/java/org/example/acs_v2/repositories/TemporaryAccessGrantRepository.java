package org.example.acs_v2.repositories;

import org.example.acs_v2.models.TemporaryAccessGrant;
import org.example.acs_v2.models.enums.AccessLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TemporaryAccessGrantRepository extends JpaRepository<TemporaryAccessGrant, Long> {
    Optional<TemporaryAccessGrant> findByApplicationIdAndUserId(Long applicationId, Long userId);

    void deleteByApplicationIdAndUserId(Long applicationId, Long userId);

    @Query("select g from TemporaryAccessGrant g where g.user.id = :userId and g.application.completed = false")
    List<TemporaryAccessGrant> findActiveByUserId(@Param("userId") Long userId);

    @Query("select max(g.accessLevel) from TemporaryAccessGrant g where g.user.id = :userId and g.application.completed = false")
    AccessLevel findMaxActiveLevelByUserId(@Param("userId") Long userId);
}

