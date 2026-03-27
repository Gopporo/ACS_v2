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

    private final AtomicBoolean registerMode = new AtomicBoolean(false);
    private final AtomicBoolean deleteMode = new AtomicBoolean(false);

    private final AtomicReference<User> pendingUser = new AtomicReference<>(null);

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

    private Zone resolveDefaultZone() {
        Zone zone = zoneRepository.findById(1L).orElse(null);
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
                        break;
                    }

                    out.write("REGISTER\n");
                    out.flush();

                    String response = in.readLine(); // ждём ответ от ESP
                    log.info("ESP response on registration: {}", response);

                    if (response != null && response.matches("\\d+")) {
                        user.setFingerprintHash(response);
                        userRepository.save(user);
                        out.write("REGISTERED\n");
                    } else {
                        out.write("ERROR\n");
                    }
                    out.flush();

                    registerMode.set(false);
                    pendingUser.set(null);
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

                boolean granted = accessRank(user.getUserAccessLvl()) >= accessRank(zone.getZoneAccessLvl());

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
        log.info("Register mode enabled for user: {}", user != null ? user.getFullName() : "null");
    }

    public void cancelRegisterMode() {
        this.registerMode.set(false);
        this.pendingUser.set(null);
        log.info("Register mode cancelled");
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
}

