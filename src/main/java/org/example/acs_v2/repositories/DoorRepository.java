package org.example.acs_v2.repositories;

import org.example.acs_v2.models.Door;
import org.example.acs_v2.models.enums.AccessLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoorRepository extends JpaRepository<Door, Long> {
    List<Door> findByAccessLevel(AccessLevel accessLevel);
}

