package org.example.acs_v2.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.models.Door;
import org.example.acs_v2.models.enums.AccessLevel;
import org.example.acs_v2.repositories.DoorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoorService {

    private final DoorRepository doorRepository;

    @Transactional
    public List<Door> getAllDoors() {
        return doorRepository.findAll();
    }

    public Door getById(Long id) {
        return doorRepository.findById(id).orElse(null);
    }

    public Door addDoor(Door door) {
        if (door.getAccessLevel() == null) {
            door.setAccessLevel(AccessLevel.UNKNOWN);
        }
        return doorRepository.save(door);
    }

    public void updateDoor(Long id, Door updated) {
        Door door = doorRepository.findById(id)
                .orElseThrow(() -> new org.example.acs_v2.exceptions.ResourceNotFoundException("Door", id));
        door.setName(updated.getName());
        door.setAccessLevel(updated.getAccessLevel());
        doorRepository.save(door);
    }

    public void deleteDoor(Long id) {
        doorRepository.deleteById(id);
    }

    public List<Door> getDoorsByAccessLevel(String accessLevel) {
        AccessLevel level = AccessLevel.valueOf(accessLevel.toUpperCase());
        return doorRepository.findByAccessLevel(level);
    }
}

