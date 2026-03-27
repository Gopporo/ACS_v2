package org.example.acs_v2.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.exceptions.ResourceNotFoundException;
import org.example.acs_v2.models.Zone;
import org.example.acs_v2.models.enums.AccessLevel;
import org.example.acs_v2.repositories.ZoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для управления зонами доступа
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;

    /**
     * Получает зону по ID
     *
     * @param id ID зоны
     * @return зона
     * @throws ResourceNotFoundException если зона не найдена
     */
    public Zone getById(Long id) {
        log.debug("Getting zone by ID: {}", id);
        return zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zone", id));

    }

    /**
     * Создает новую зону
     *
     * @param zone зона для создания
     * @return true если зона создана, false если зона с таким именем уже существует
     */
    public boolean createZone(Zone zone) {
        log.debug("Creating new zone with name: {}", zone.getName());
        String zoneName = zone.getName();
        if (zoneRepository.findByName(zoneName) != null) {
            log.warn("Zone with name '{}' already exists", zoneName);
            return false;
        }
        log.debug("Zone name '{}' is available", zoneName);

        log.info("Saving new Zone with name: {}", zoneName);
        zoneRepository.save(zone);
        log.info("Zone '{}' successfully saved", zoneName);
        return true;
    }

    /**
     * Получает список всех зон
     *
     * @return список зон
     */
    public List<Zone> list() {
        log.debug("Getting all zones");
        return zoneRepository.findAll();
    }

    /**
     * Получает зоны по уровню доступа
     *
     * @param zoneAccessLvl уровень доступа зоны
     * @return список зон с указанным уровнем доступа
     */
    public List<Zone> getZonesByAccessLvl(AccessLevel zoneAccessLvl) {
        log.debug("Getting zones by access level: {}", zoneAccessLvl);
        return zoneRepository.findZonesByZoneAccessLvl(zoneAccessLvl);
    }

    /**
     * Получает зоны по имени
     *
     * @param name имя зоны для поиска
     * @return список зон с указанным именем
     */
    public List<Zone> getZoneByName(String name) {
        log.debug("Getting zones by name: {}", name);
        return zoneRepository.findZonesByName(name);
    }

    public Zone save(Zone zone) {
        return zoneRepository.save(zone);
    }

    public void deleteById(Long id) {
        zoneRepository.deleteById(id);
    }
}
