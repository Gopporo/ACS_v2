package org.example.acs_v2.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.models.Worker;
import org.example.acs_v2.models.enums.AccessLevel;
import org.example.acs_v2.models.enums.Status;
import org.example.acs_v2.repositories.WorkerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkerService {

    private final WorkerRepository workerRepository;

    public List<Worker> getAllWorkers() {
        return workerRepository.findAll().stream()
                // "Unknown" служебный работник для отказов
                .filter(worker -> worker.getFirstName() == null || !"Unknown".equalsIgnoreCase(worker.getFirstName()))
                .toList();
    }

    public Worker getById(Long id) {
        return workerRepository.findById(id).orElse(null);
    }

    public Worker addWorker(Worker worker) {
        if (worker.getStatus() == null) {
            worker.setStatus(Status.ACTIVE);
        }
        if (worker.getAccessLevel() == null) {
            worker.setAccessLevel(AccessLevel.UNKNOWN);
        }
        return workerRepository.save(worker);
    }

    @Transactional
    public void updateWorker(Long id, Worker updated) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new org.example.acs_v2.exceptions.ResourceNotFoundException("Worker", id));

        worker.setFirstName(updated.getFirstName());
        worker.setLastName(updated.getLastName());
        worker.setSurname(updated.getSurname());
        worker.setNumberPhone(updated.getNumberPhone());
        worker.setAccessLevel(updated.getAccessLevel());
        if (updated.getStatus() != null) {
            worker.setStatus(updated.getStatus());
        }
        // fingerprintHash обновляем только через TCP сервер режим регистрации

        workerRepository.save(worker);
    }

    public void deleteWorker(Long id) {
        workerRepository.deleteById(id);
    }

    public List<Worker> getWorkersByAccessLevel(String accessLevel) {
        AccessLevel level = AccessLevel.valueOf(accessLevel.toUpperCase());
        return workerRepository.findByAccessLevel(level).stream()
                .filter(worker -> worker.getFirstName() == null || !"Unknown".equalsIgnoreCase(worker.getFirstName()))
                .toList();
    }

    public Worker findByFingerprintHash(String fingerprintHash) {
        return workerRepository.findByFingerprintHash(fingerprintHash);
    }

    public Worker findByFirstName(String firstName) {
        return workerRepository.findByFirstName(firstName);
    }
}

