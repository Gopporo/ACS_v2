package org.example.acs_v2.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.models.AccessAttempt;
import org.example.acs_v2.models.User;
import org.example.acs_v2.models.Zone;
import org.example.acs_v2.models.enums.AccessLevel;
import org.example.acs_v2.models.enums.Status;
import org.example.acs_v2.repositories.AccessAttemptRepository;
import org.example.acs_v2.repositories.UserRepository;
import org.example.acs_v2.repositories.ZoneRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ZoneRepository zoneRepository;
    private final AccessAttemptRepository accessAttemptRepository;

    @Override
    public void run(String... args) {
        ensureUsers();
        ensureZones();
        ensureUnknownUser();
        ensureAttempts();
    }

    private void ensureUsers() {
        if (userRepository.count() != 0) {
            return;
        }

        User w1 = new User();
        w1.setFirstName("Иван");
        w1.setLastName("Иванов");
        w1.setSurname("Иванович");
        w1.setUserAccessLvl(AccessLevel.LEVEL_4);
        w1.setStatus(Status.ACTIVE);
        w1.setNumberPhone("+71234567890");
        w1.setFingerprintHash("fingerprintHash1");

        User w2 = new User();
        w2.setFirstName("Мария");
        w2.setLastName("Петрова");
        w2.setSurname("Сергеевна");
        w2.setUserAccessLvl(AccessLevel.LEVEL_2);
        w2.setStatus(Status.ACTIVE);
        w2.setNumberPhone("+79876543210");
        w2.setFingerprintHash("fingerprintHash2");

        userRepository.save(w1);
        userRepository.save(w2);
        log.info("Seed users created");
    }

    private void ensureZones() {
        if (zoneRepository.count() != 0) {
            return;
        }

        Zone d1 = new Zone();
        d1.setName("Серверная");
        d1.setZoneAccessLvl(AccessLevel.LEVEL_4);

        Zone d2 = new Zone();
        d2.setName("Кладовка");
        d2.setZoneAccessLvl(AccessLevel.LEVEL_2);

        zoneRepository.save(d1);
        zoneRepository.save(d2);
        log.info("Seed zones created");
    }

    private void ensureUnknownUser() {
        if (userRepository.findByName("Unknown") != null) {
            return;
        }

        User unknown = new User();
        unknown.setName("Unknown");
        unknown.setUserAccessLvl(AccessLevel.LEVEL_1);
        unknown.setStatus(Status.ACTIVE);
        unknown.setNumberPhone(null);
        unknown.setFingerprintHash(null);
        userRepository.save(unknown);
        log.info("Unknown user seeded");
    }

    private void ensureAttempts() {
        if (accessAttemptRepository.count() != 0) {
            return;
        }

        User user1 = userRepository.findByFingerprintHash("fingerprintHash1");
        User user2 = userRepository.findByFingerprintHash("fingerprintHash2");

        Zone zone1 = zoneRepository.findAll().stream()
                .filter(d -> "Серверная".equalsIgnoreCase(d.getName()))
                .findFirst()
                .orElseGet(() -> zoneRepository.findAll().stream().findFirst().orElse(null));

        Zone zone2 = zoneRepository.findAll().stream()
                .filter(d -> "Кладовка".equalsIgnoreCase(d.getName()))
                .findFirst()
                .orElseGet(() -> zoneRepository.findAll().stream().findFirst().orElse(null));

        if (user1 != null && zone1 != null) {
            AccessAttempt attempt1 = new AccessAttempt();
            attempt1.setUser(user1);
            attempt1.setZone(zone1);
            attempt1.setSuccess(true);
            attempt1.setTimestamp(LocalDateTime.of(2025, 2, 11, 12, 45));
            accessAttemptRepository.save(attempt1);
        }

        if (user2 != null && zone2 != null) {
            AccessAttempt attempt2 = new AccessAttempt();
            attempt2.setUser(user2);
            attempt2.setZone(zone2);
            attempt2.setSuccess(false);
            attempt2.setTimestamp(LocalDateTime.of(2025, 2, 11, 12, 45));
            accessAttemptRepository.save(attempt2);
        }

        log.info("Seed access attempts created");
    }
}

