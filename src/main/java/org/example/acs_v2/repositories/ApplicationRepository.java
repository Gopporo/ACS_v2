package org.example.acs_v2.repositories;

import org.example.acs_v2.models.Application;
import org.example.acs_v2.models.User;
import org.example.acs_v2.models.enums.AccessLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findApplicationsByAccessLevelAndCompletedFalse(AccessLevel accessLvl);
    List<Application> findApplicationsByNameAndCompletedFalse(String name);
    List<Application> findByUserIdIsNullAndCompletedFalse();
    List<Application> findAllByUserIdAndCompletedFalse(Long userId);
    List<Application> findAllByCompletedFalse();
    Application findByIdAndCompletedFalse(Long id);
}
