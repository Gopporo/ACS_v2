package org.example.acs_v2.repositories;

import org.example.acs_v2.models.Worker;
import org.example.acs_v2.models.enums.AccessLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {
    Worker findByFingerprintHash(String fingerprintHash);

    List<Worker> findByAccessLevel(AccessLevel accessLevel);

    Worker findByFirstName(String firstName);
}

