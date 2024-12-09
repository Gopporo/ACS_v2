package org.example.acs_v2.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.models.User;
import org.example.acs_v2.models.Zone;
import org.example.acs_v2.models.enums.Role;
import org.example.acs_v2.repositories.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ZoneService {

    @Autowired
    ZoneRepository zoneRepository;

    public Zone getById(Long id) {

        return zoneRepository.findById(id).orElse(null);

    }

    public boolean createZone(Zone zone) {
        System.out.println("Сервис для создания зоны вызвал");
        String zoneName = zone.getName();
        if (zoneRepository.findByName(zoneName) != null)
            return false;
        System.out.println("Название зоны проверил");

        log.info("Saving new Zine with name: {}", zoneName);
        zoneRepository.save(zone);
        System.out.println("Зону сохранил сохранил");
        return true;
    }

    public List<Zone> list() {
        return zoneRepository.findAll();
    }

    public List<Zone> getZonesByAccessLvl(int zoneAccessLvl) {
        return zoneRepository.findZonesByZoneAccessLvl(zoneAccessLvl);
    }

    public List<Zone> getZoneByName(String name) {
        return zoneRepository.findZonesByName(name);
    }
}
