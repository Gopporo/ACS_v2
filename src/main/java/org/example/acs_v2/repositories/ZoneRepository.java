package org.example.acs_v2.repositories;

import org.example.acs_v2.models.User;
import org.example.acs_v2.models.Zone;
import org.example.acs_v2.models.enums.AccessLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Zone findByName(String name);
    List<Zone> findZonesByZoneAccessLvl(AccessLevel zoneAccessLvl);
    List<Zone> findZonesByName(String name);
}
