package org.example.acs_v2.tcp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.models.AccessAttempt;
import org.example.acs_v2.models.User;
import org.example.acs_v2.models.Zone;
import org.example.acs_v2.models.enums.AccessLevel;
import org.example.acs_v2.repositories.AccessAttemptRepository;
import org.example.acs_v2.repositories.UserRepository;
import org.example.acs_v2.repositories.ZoneRepository;
import org.example.acs_v2.exceptions.ResourceNotFoundException;
import org.example.acs_v2.services.TemporaryAccessService;
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

    private final UserRepository userRepository;
    private final ZoneRepository zoneRepository;
    private final AccessAttemptRepository attemptRepository;
    private final TemporaryAccessService temporaryAccessService;

    private final AtomicBoolean registerMode = new AtomicBoolean(false);
    private final AtomicBoolean deleteMode = new AtomicBoolean(false);
    private final AtomicReference<Long> scannerZoneId = new AtomicReference<>(1L);

    private final AtomicReference<User> pendingUser = new AtomicReference<>(null);
    private final AtomicReference<String> pendingOldFingerprint = new AtomicReference<>(null);

    public enum RegistrationState {
        IDLE,
        IN_PROGRESS,
        SUCCESS,
        ERROR,
        CANCELLED
    }

    private final AtomicReference<Long> registrationUserId = new AtomicReference<>(null);
    private final AtomicReference<RegistrationState> registrationState = new AtomicReference<>(RegistrationState.IDLE);
    private final AtomicReference<String> registrationMessage = new AtomicReference<>(null);

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
        return level == null ? 0 : level.getRank();
    }

    private int effectiveUserRank(User user) {
        AccessLevel temp = temporaryAccessService.getMaxActiveTemporaryLevel(user.getId());
        return Math.max(accessRank(user.getUserAccessLvl()), accessRank(temp));
    }

    private Zone resolveDefaultZone() {
        Zone zone = zoneRepository.findById(scannerZoneId.get()).orElse(null);
        if (zone != null) {
            return zone;
        }
        return zoneRepository.findAll().stream().findFirst().orElse(null);
    }

    private User ensureUnknownUser() {
        // "Unknown" с ESP должен попадать в AccessAttempt, но НЕ создавать пользователя в БД.
        return null;
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
                    User user = pendingUser.get();
                    if (user == null) {
                        out.write("ERROR_NO_WORKER\n");
                        out.flush();
                        registerMode.set(false);
                        pendingUser.set(null);
                        pendingOldFingerprint.set(null);
                        registrationState.set(RegistrationState.ERROR);
                        registrationMessage.set("ERROR_NO_WORKER");
                        break;
                    }

                    String old = pendingOldFingerprint.get();
                    if (old != null && !old.isBlank() && old.matches("\\d+")) {
                        out.write("REGISTER " + old + "\n");
                    } else {
                        out.write("REGISTER\n");
                    }
                    out.flush();

                    String response = in.readLine(); // ждём ответ от ESP
                    log.info("ESP response on registration: {}", response);

                    if (response != null && response.matches("\\d+")) {
                        user.setFingerprintHash(response);
                        userRepository.save(user);
                        out.write("REGISTERED\n");
                        registrationState.set(RegistrationState.SUCCESS);
                        registrationMessage.set("REGISTERED");
                    } else {
                        out.write("ERROR\n");
                        registrationState.set(RegistrationState.ERROR);
                        registrationMessage.set(response == null ? "NO_RESPONSE" : response);
                    }
                    out.flush();

                    registerMode.set(false);
                    pendingUser.set(null);
                    pendingOldFingerprint.set(null);
                    break;
                }

                // === Delete mode ===
                if (deleteMode.get()) {
                    User user = pendingUser.get();
                    if (user == null || user.getFingerprintHash() == null) {
                        out.write("ERROR_NO_FINGERPRINT\n");
                        out.flush();
                        deleteMode.set(false);
                        pendingUser.set(null);
                        break;
                    }

                    out.write("DELETE " + user.getFingerprintHash() + "\n");
                    out.flush();

                    String response = in.readLine();
                    log.info("ESP response on delete: {}", response);

                    if ("DELETED".equalsIgnoreCase(response)) {
                        userRepository.delete(user);
                        out.write("DELETE_OK\n");
                    } else {
                        out.write("DELETE_ERROR\n");
                    }
                    out.flush();

                    deleteMode.set(false);
                    pendingUser.set(null);
                    break;
                }

                // === Normal mode ===
                if ("HELLO_FROM_ESP".equalsIgnoreCase(message)) {
                    continue;
                }

                // "Unknown" fingerprint - always deny and save as attempt
                if ("Unknown".equalsIgnoreCase(message)) {
                    User unknown = ensureUnknownUser();
                    Zone zone = resolveDefaultZone();
                    if (zone == null) {
                        out.write("DOOR_NOT_FOUND\n");
                        out.flush();
                        break;
                    }

                    AccessAttempt attempt = new AccessAttempt();
                    attempt.setTimestamp(LocalDateTime.now());
                    attempt.setZone(zone);
                    attempt.setUser(unknown);
                    attempt.setSuccess(false);
                    attemptRepository.save(attempt);

                    out.write("ACCESS_DENIED\n");
                    out.flush();
                    break;
                }

                // Known fingerprint
                User user = userRepository.findByFingerprintHash(message);
                if (user == null) {
                    out.write("NOT_FOUND\n");
                    out.flush();
                    break;
                }

                Zone zone = resolveDefaultZone();
                if (zone == null) {
                    out.write("DOOR_NOT_FOUND\n");
                    out.flush();
                    break;
                }

                boolean granted = effectiveUserRank(user) >= accessRank(zone.getZoneAccessLvl());

                AccessAttempt attempt = new AccessAttempt();
                attempt.setTimestamp(LocalDateTime.now());
                attempt.setUser(user);
                attempt.setZone(zone);
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

    public void enableRegisterMode(User user) {
        this.registerMode.set(true);
        this.deleteMode.set(false);
        this.pendingUser.set(user);
        this.pendingOldFingerprint.set(null);
        this.registrationUserId.set(user != null ? user.getId() : null);
        this.registrationState.set(RegistrationState.IN_PROGRESS);
        this.registrationMessage.set(null);
        log.info("Register mode enabled for user: {}", user != null ? user.getFullName() : "null");
    }

    public void enableRegisterModeReplace(User user, String oldFingerprintId) {
        this.registerMode.set(true);
        this.deleteMode.set(false);
        this.pendingUser.set(user);
        this.pendingOldFingerprint.set(oldFingerprintId);
        this.registrationUserId.set(user != null ? user.getId() : null);
        this.registrationState.set(RegistrationState.IN_PROGRESS);
        this.registrationMessage.set(null);
        log.info("Register replace mode enabled for user: {}", user != null ? user.getFullName() : "null");
    }

    public void cancelRegisterMode() {
        this.registerMode.set(false);
        this.pendingUser.set(null);
        this.pendingOldFingerprint.set(null);
        this.registrationState.set(RegistrationState.CANCELLED);
        this.registrationMessage.set("CANCELLED");
        log.info("Register mode cancelled");
    }

    public RegistrationState getRegistrationState(Long userId) {
        if (userId != null && userId.equals(registrationUserId.get())) {
            return registrationState.get();
        }
        return RegistrationState.IDLE;
    }

    public String getRegistrationMessage(Long userId) {
        if (userId != null && userId.equals(registrationUserId.get())) {
            return registrationMessage.get();
        }
        return null;
    }

    public void enableDeleteMode(User user) {
        this.deleteMode.set(true);
        this.registerMode.set(false);
        this.pendingUser.set(user);
        log.info("Delete mode enabled for user: {}", user != null ? user.getFullName() : "null");
    }

    public void cancelDeleteMode() {
        this.deleteMode.set(false);
        this.pendingUser.set(null);
        log.info("Delete mode cancelled");
    }

    public boolean isRegisterModeEnabled() {
        return registerMode.get();
    }

    public void setScannerZoneId(Long zoneId) {
        if (!zoneRepository.existsById(zoneId)) {
            throw new ResourceNotFoundException("Zone", zoneId);
        }
        scannerZoneId.set(zoneId);
        log.info("Scanner zone id set to {}", zoneId);
    }

    public Long getScannerZoneId() {
        return scannerZoneId.get();
    }
}

