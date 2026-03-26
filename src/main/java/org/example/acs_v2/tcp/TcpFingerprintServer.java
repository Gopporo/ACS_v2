package org.example.acs_v2.tcp;

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
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class TcpFingerprintServer {

    private final WorkerRepository workerRepository;
    private final DoorRepository doorRepository;
    private final AccessAttemptRepository attemptRepository;

    private final AtomicBoolean registerMode = new AtomicBoolean(false);
    private final AtomicBoolean deleteMode = new AtomicBoolean(false);

    private final AtomicReference<Worker> pendingWorker = new AtomicReference<>(null);
    private final AtomicReference<Worker> unknownWorker = new AtomicReference<>(null);

    @PostConstruct
    public void init() {
        startServer();
    }

    public void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(4444)) {
                log.info("Fingerprint TCP server started on port 4444");
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(() -> handleClient(clientSocket)).start();
                }
            } catch (IOException e) {
                log.error("Error starting TCP server", e);
            }
        }).start();
    }

    private int accessRank(AccessLevel level) {
        if (level == null) {
            return 0;
        }
        return switch (level) {
            case GUEST -> 0;
            case EMPLOYEE -> 1;
            case ADMINISTRATION -> 2;
            case UNKNOWN -> -1; // UNKNOWN должен быть минимальным
        };
    }

    private Door resolveDefaultDoor() {
        Door door = doorRepository.findById(1L).orElse(null);
        if (door != null) {
            return door;
        }
        return doorRepository.findAll().stream().findFirst().orElse(null);
    }

    private Worker ensureUnknownWorker() {
        Worker unknown = workerRepository.findByFirstName("Unknown");
        if (unknown == null) {
            unknown = new Worker();
            unknown.setFirstName("Unknown");
            unknown.setLastName("");
            unknown.setSurname("");
            unknown.setAccessLevel(AccessLevel.UNKNOWN);
            unknown.setStatus(Status.ACTIVE);
            // fingerprintHash неизвестен
            workerRepository.save(unknown);
        }
        unknownWorker.set(unknown);
        return unknown;
    }

    private void handleClient(Socket socket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        ) {
            log.info("TCP client connected: {}", socket.getRemoteSocketAddress());

            while (true) {
                String message = in.readLine();
                if (message == null) {
                    log.warn("TCP client closed connection");
                    break;
                }

                message = message.trim();
                log.info("Message from ESP: '{}'", message);

                // === Register mode ===
                if (registerMode.get()) {
                    Worker worker = pendingWorker.get();
                    if (worker == null) {
                        out.write("ERROR_NO_WORKER\n");
                        out.flush();
                        registerMode.set(false);
                        pendingWorker.set(null);
                        break;
                    }

                    out.write("REGISTER\n");
                    out.flush();

                    String response = in.readLine(); // ждём ответ от ESP
                    log.info("ESP response on registration: {}", response);

                    if (response != null && response.matches("\\d+")) {
                        worker.setFingerprintHash(response);
                        workerRepository.save(worker);
                        out.write("REGISTERED\n");
                    } else {
                        out.write("ERROR\n");
                    }
                    out.flush();

                    registerMode.set(false);
                    pendingWorker.set(null);
                    break;
                }

                // === Delete mode ===
                if (deleteMode.get()) {
                    Worker worker = pendingWorker.get();
                    if (worker == null || worker.getFingerprintHash() == null) {
                        out.write("ERROR_NO_FINGERPRINT\n");
                        out.flush();
                        deleteMode.set(false);
                        pendingWorker.set(null);
                        break;
                    }

                    out.write("DELETE " + worker.getFingerprintHash() + "\n");
                    out.flush();

                    String response = in.readLine();
                    log.info("ESP response on delete: {}", response);

                    if ("DELETED".equalsIgnoreCase(response)) {
                        workerRepository.delete(worker);
                        out.write("DELETE_OK\n");
                    } else {
                        out.write("DELETE_ERROR\n");
                    }
                    out.flush();

                    deleteMode.set(false);
                    pendingWorker.set(null);
                    break;
                }

                // === Normal mode ===
                if ("HELLO_FROM_ESP".equalsIgnoreCase(message)) {
                    continue;
                }

                // "Unknown" fingerprint - always deny and save as attempt
                if ("Unknown".equalsIgnoreCase(message)) {
                    Worker unknown = ensureUnknownWorker();
                    Door door = resolveDefaultDoor();
                    if (door == null) {
                        out.write("DOOR_NOT_FOUND\n");
                        out.flush();
                        break;
                    }

                    AccessAttempt attempt = new AccessAttempt();
                    attempt.setTimestamp(LocalDateTime.now());
                    attempt.setDoor(door);
                    attempt.setWorker(unknown);
                    attempt.setSuccess(false);
                    attemptRepository.save(attempt);

                    out.write("ACCESS_DENIED\n");
                    out.flush();
                    break;
                }

                // Known fingerprint
                Worker worker = workerRepository.findByFingerprintHash(message);
                if (worker == null) {
                    out.write("NOT_FOUND\n");
                    out.flush();
                    break;
                }

                Door door = resolveDefaultDoor();
                if (door == null) {
                    out.write("DOOR_NOT_FOUND\n");
                    out.flush();
                    break;
                }

                boolean granted = accessRank(worker.getAccessLevel()) >= accessRank(door.getAccessLevel());

                AccessAttempt attempt = new AccessAttempt();
                attempt.setTimestamp(LocalDateTime.now());
                attempt.setWorker(worker);
                attempt.setDoor(door);
                attempt.setSuccess(granted);
                attemptRepository.save(attempt);

                out.write(granted ? "ACCESS_GRANTED\n" : "ACCESS_DENIED\n");
                out.flush();
                break;
            }
        } catch (Exception e) {
            log.error("TCP client handling error", e);
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    public void enableRegisterMode(Worker worker) {
        this.registerMode.set(true);
        this.deleteMode.set(false);
        this.pendingWorker.set(worker);
        log.info("Register mode enabled for worker: {}", worker != null ? worker.getFullName() : "null");
    }

    public void cancelRegisterMode() {
        this.registerMode.set(false);
        this.pendingWorker.set(null);
        log.info("Register mode cancelled");
    }

    public void enableDeleteMode(Worker worker) {
        this.deleteMode.set(true);
        this.registerMode.set(false);
        this.pendingWorker.set(worker);
        log.info("Delete mode enabled for worker: {}", worker != null ? worker.getFullName() : "null");
    }

    public void cancelDeleteMode() {
        this.deleteMode.set(false);
        this.pendingWorker.set(null);
        log.info("Delete mode cancelled");
    }

    public boolean isRegisterModeEnabled() {
        return registerMode.get();
    }
}

