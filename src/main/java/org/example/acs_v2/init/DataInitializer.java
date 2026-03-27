package org.example.acs_v2.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.models.AccessAttempt;
import org.example.acs_v2.models.Role;
import org.example.acs_v2.models.User;
import org.example.acs_v2.models.Zone;
import org.example.acs_v2.models.enums.AccessLevel;
import org.example.acs_v2.models.enums.Status;
import org.example.acs_v2.repositories.AccessAttemptRepository;
import org.example.acs_v2.repositories.UserRepository;
import org.example.acs_v2.repositories.ZoneRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ZoneRepository zoneRepository;
    private final AccessAttemptRepository accessAttemptRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        ensureUsersForAllRoles();
        ensureDemoUsersForAccessAttempts();
        ensureZones();
        ensureUnknownUser();
        ensureAttempts();
    }

    private void ensureUsersForAllRoles() {
        createRoleUserIfAbsent(
                "admin@acs.local",
                "+375291111111",
                "Администратор Системы",
                "Системный администратор",
                AccessLevel.LEVEL_5,
                Role.ROLE_ADMIN
        );

        createRoleUserIfAbsent(
                "director@acs.local",
                "+375292222222",
                "Директор Отдела",
                "Руководитель отдела",
                AccessLevel.LEVEL_4,
                Role.ROLE_DIRECTOR
        );

        createRoleUserIfAbsent(
                "security@acs.local",
                "+375293333333",
                "Сотрудник Охраны",
                "Сотрудник безопасности",
                AccessLevel.LEVEL_3,
                Role.ROLE_SECURITY
        );

        createRoleUserIfAbsent(
                "user@acs.local",
                "+375294444444",
                "Обычный Сотрудник",
                "Сотрудник",
                AccessLevel.LEVEL_2,
                Role.ROLE_USER
        );
    }

    private void createRoleUserIfAbsent(String email,
                                        String phone,
                                        String fullName,
                                        String position,
                                        AccessLevel accessLevel,
                                        Role role) {
        if (userRepository.findByEmail(email) != null) {
            return;
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("12345"));
        user.setActive(true);
        user.setApproved(true);
        user.setStatus(Status.ACTIVE);
        user.setNumberPhone(phone);
        user.setPosition(position);
        user.setUserAccessLvl(accessLevel);
        user.setName(fullName);
        user.setRoles(Set.of(role));
        userRepository.save(user);
        log.info("Seed user for role {} created: {}", role, email);
    }

    private void ensureDemoUsersForAccessAttempts() {
        if (userRepository.findByFingerprintHash("fingerprintHash1") == null) {
            User user = new User();
            user.setEmail("fingerprint1@acs.local");
            user.setPassword(passwordEncoder.encode("12345"));
            user.setActive(true);
            user.setApproved(true);
            user.setStatus(Status.ACTIVE);
            user.setNumberPhone("+375295555551");
            user.setPosition("Тестовый сотрудник 1");
            user.setUserAccessLvl(AccessLevel.LEVEL_4);
            user.setRoles(Set.of(Role.ROLE_USER));
            user.setName("Иван Иванов Иванович");
            user.setFingerprintHash("fingerprintHash1");
            userRepository.save(user);
        }

        if (userRepository.findByFingerprintHash("fingerprintHash2") == null) {
            User user = new User();
            user.setEmail("fingerprint2@acs.local");
            user.setPassword(passwordEncoder.encode("12345"));
            user.setActive(true);
            user.setApproved(true);
            user.setStatus(Status.ACTIVE);
            user.setNumberPhone("+375295555552");
            user.setPosition("Тестовый сотрудник 2");
            user.setUserAccessLvl(AccessLevel.LEVEL_2);
            user.setRoles(Set.of(Role.ROLE_USER));
            user.setName("Мария Петрова Сергеевна");
            user.setFingerprintHash("fingerprintHash2");
            userRepository.save(user);
        }
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

    /* private void ensureUnknownUser() {
        if (userRepository.findByName("Unknown") != null) {
            return;
        }

        User unknown = new User();
        unknown.setEmail("unknown@acs.local");
        unknown.setPassword(passwordEncoder.encode("12345"));
        unknown.setActive(true);
        unknown.setApproved(true);
        unknown.setName("Unknown");
        unknown.setUserAccessLvl(AccessLevel.LEVEL_1);
        unknown.setStatus(Status.ACTIVE);
        unknown.setNumberPhone(null);
        unknown.setFingerprintHash(null);
        unknown.setRoles(Set.of(Role.ROLE_USER));
        userRepository.save(unknown);
        log.info("Unknown user seeded");
    } */

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

