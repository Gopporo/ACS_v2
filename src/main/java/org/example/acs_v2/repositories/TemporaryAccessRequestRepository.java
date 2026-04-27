package org.example.acs_v2.repositories;

import org.example.acs_v2.models.TemporaryAccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TemporaryAccessRequestRepository extends JpaRepository<TemporaryAccessRequest, Long> {
    Optional<TemporaryAccessRequest> findByApplicationIdAndRequesterId(Long applicationId, Long requesterId);

    List<TemporaryAccessRequest> findAllByRequesterDepartmentIdOrderByCreatedAtDesc(Long departmentId);

    boolean existsByApplicationId(Long applicationId);

    @Query("select distinct r.application.id from TemporaryAccessRequest r")
    List<Long> findAllRequestedApplicationIds();
}

