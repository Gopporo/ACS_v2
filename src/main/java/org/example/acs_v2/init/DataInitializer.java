package org.example.acs_v2.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.models.AccessAttempt;
import org.example.acs_v2.models.Door;
import org.example.acs_v2.models.Worker;
import org.example.acs_v2.models.enums.AccessLevel;
import org.example.acs_v2.models.enums.Status;
import org.example.acs_v2.repositories.AccessAttemptRepository;
import org.example.acs_v2.repositories.DoorRepository;
import org.example.acs_v2.repositories.WorkerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final WorkerRepository workerRepository;
    private final DoorRepository doorRepository;
    private final AccessAttemptRepository accessAttemptRepository;

    @Override
    public void run(String... args) {
        ensureWorkers();
        ensureDoors();
        ensureUnknownWorker();
        ensureAttempts();
    }

    private void ensureWorkers() {
        if (workerRepository.count() != 0) {
            return;
        }

        Worker w1 = new Worker();
        w1.setFirstName("Иван");
        w1.setLastName("Иванов");
        w1.setSurname("Иванович");
        w1.setAccessLevel(AccessLevel.ADMINISTRATION);
        w1.setStatus(Status.ACTIVE);
        w1.setNumberPhone("+71234567890");
        w1.setFingerprintHash("fingerprintHash1");

        Worker w2 = new Worker();
        w2.setFirstName("Мария");
        w2.setLastName("Петрова");
        w2.setSurname("Сергеевна");
        w2.setAccessLevel(AccessLevel.EMPLOYEE);
        w2.setStatus(Status.ACTIVE);
        w2.setNumberPhone("+79876543210");
        w2.setFingerprintHash("fingerprintHash2");

        workerRepository.save(w1);
        workerRepository.save(w2);
        log.info("Seed workers created");
    }

    private void ensureDoors() {
        if (doorRepository.count() != 0) {
            return;
        }

        Door d1 = new Door();
        d1.setName("Серверная");
        d1.setAccessLevel(AccessLevel.ADMINISTRATION);

        Door d2 = new Door();
        d2.setName("Кладовка");
        d2.setAccessLevel(AccessLevel.EMPLOYEE);

        doorRepository.save(d1);
        doorRepository.save(d2);
        log.info("Seed doors created");
    }

    private void ensureUnknownWorker() {
        if (workerRepository.findByFirstName("Unknown") != null) {
            return;
        }

        Worker unknown = new Worker();
        unknown.setFirstName("Unknown");
        unknown.setLastName("");
        unknown.setSurname("");
        unknown.setAccessLevel(AccessLevel.UNKNOWN);
        unknown.setStatus(Status.ACTIVE);
        unknown.setNumberPhone(null);
        unknown.setFingerprintHash(null);
        workerRepository.save(unknown);
        log.info("Unknown worker seeded");
    }

    private void ensureAttempts() {
        if (accessAttemptRepository.count() != 0) {
            return;
        }

        Worker worker1 = workerRepository.findByFingerprintHash("fingerprintHash1");
        Worker worker2 = workerRepository.findByFingerprintHash("fingerprintHash2");

        Door door1 = doorRepository.findAll().stream()
                .filter(d -> "Серверная".equalsIgnoreCase(d.getName()))
                .findFirst()
                .orElseGet(() -> doorRepository.findAll().stream().findFirst().orElse(null));

        Door door2 = doorRepository.findAll().stream()
                .filter(d -> "Кладовка".equalsIgnoreCase(d.getName()))
                .findFirst()
                .orElseGet(() -> doorRepository.findAll().stream().findFirst().orElse(null));

        if (worker1 != null && door1 != null) {
            AccessAttempt attempt1 = new AccessAttempt();
            attempt1.setWorker(worker1);
            attempt1.setDoor(door1);
            attempt1.setSuccess(true);
            attempt1.setTimestamp(LocalDateTime.of(2025, 2, 11, 12, 45));
            accessAttemptRepository.save(attempt1);
        }

        if (worker2 != null && door2 != null) {
            AccessAttempt attempt2 = new AccessAttempt();
            attempt2.setWorker(worker2);
            attempt2.setDoor(door2);
            attempt2.setSuccess(false);
            attempt2.setTimestamp(LocalDateTime.of(2025, 2, 11, 12, 45));
            accessAttemptRepository.save(attempt2);
        }

        log.info("Seed access attempts created");
    }
}

