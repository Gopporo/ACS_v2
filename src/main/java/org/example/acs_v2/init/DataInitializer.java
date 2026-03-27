package org.example.acs_v2.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.models.AccessAttempt;
import org.example.acs_v2.models.Role;
import org.example.acs_v2.models.User;
import org.example.acs_v2.models.Zone;
import org.example.acs_v2.models.Department;
import org.example.acs_v2.models.enums.AccessLevel;
import org.example.acs_v2.models.enums.Status;
import org.example.acs_v2.repositories.AccessAttemptRepository;
import org.example.acs_v2.repositories.DepartmentRepository;
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
    private final DepartmentRepository departmentRepository;
    private final AccessAttemptRepository accessAttemptRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Для "дропа БД" это нужно оставлять идемпотентным: повторный запуск не должен создавать дубликаты.
        if (userRepository.count() != 0
                || zoneRepository.count() != 0
                || departmentRepository.count() != 0
                || accessAttemptRepository.count() != 0) {
            return;
        }

        ensureDepartments();
        User admin = createRoleUser("admin@acs.local",
                "Администратор Системы",
                "+375291111111",
                "Системный администратор",
                AccessLevel.LEVEL_5,
                Role.ROLE_ADMIN,
                departmentRepository.findByName("Администрация"));

        User director = createRoleUser("director@acs.local",
                "Директор Отдела",
                "+375292222222",
                "Руководитель отдела",
                AccessLevel.LEVEL_4,
                Role.ROLE_DIRECTOR,
                departmentRepository.findByName("Без отдела"));

        User security = createRoleUser("security@acs.local",
                "Сотрудник Охраны",
                "+375293333333",
                "Сотрудник безопасности",
                AccessLevel.LEVEL_3,
                Role.ROLE_SECURITY,
                departmentRepository.findByName("Безопасность"));

        User user = createRoleUser("user@acs.local",
                "Обычный Сотрудник",
                "+375294444444",
                "Сотрудник",
                AccessLevel.LEVEL_2,
                Role.ROLE_USER,
                departmentRepository.findByName("Склад"));

        // На всякий случай: department_id=5 в native-запросе у UserRepository использует "Без отдела"
        // как "сентинел" (пустой отдел).
        Department noneDept = departmentRepository.findByName("Без отдела");
        if (noneDept != null) {
            noneDept.setHead(admin);
            departmentRepository.save(noneDept);
        }

        ensureZones();
        ensureAttempts(admin, security, user);
    }

    private void ensureDepartments() {
        if (departmentRepository.count() != 0) {
            return;
        }

        Department it = new Department();
        it.setName("IT");
        departmentRepository.save(it);

        Department warehouse = new Department();
        warehouse.setName("Склад");
        departmentRepository.save(warehouse);

        Department securityDept = new Department();
        securityDept.setName("Безопасность");
        departmentRepository.save(securityDept);

        Department administration = new Department();
        administration.setName("Администрация");
        departmentRepository.save(administration);

        // Важно: в UserRepository есть nativeQuery с WHERE u.department_id = 5.
        // Поэтому "Без отдела" должен быть 5-м департаментом, если БД пустая.
        Department none = new Department();
        none.setName("Без отдела");
        departmentRepository.save(none);
    }

    private User createRoleUser(String email,
                                 String fullName,
                                 String phone,
                                 String position,
                                 AccessLevel accessLevel,
                                 Role role,
                                 Department department) {
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
        // Для TCP-сервера нужен отпечаток, иначе доступы будут всегда "Unknown".
        user.setFingerprintHash(fingerprintForRole(role));
        user.setRoles(Set.of(role));
        user.setDepartment(department);
        userRepository.save(user);
        log.info("Seed role user {} created: {}", role, email);
        return user;
    }

    private String fingerprintForRole(Role role) {
        return switch (role) {
            // ESP ожидает числовую строку (см. TcpFingerprintServer: matches("\\d+"))
            case ROLE_ADMIN -> "90001";
            case ROLE_DIRECTOR -> "90002";
            case ROLE_SECURITY -> "90003";
            case ROLE_USER -> "90004";
        };
    }

    private void ensureZones() {
        if (zoneRepository.count() != 0) {
            return;
        }

        Zone d1 = new Zone();
        d1.setName("Серверная");
        d1.setDisc("Доступ к серверной");
        d1.setZoneAccessLvl(AccessLevel.LEVEL_4);

        Zone d2 = new Zone();
        d2.setName("Кладовка");
        d2.setDisc("Доступ к складу");
        d2.setZoneAccessLvl(AccessLevel.LEVEL_2);

        Zone d3 = new Zone();
        d3.setName("Архив");
        d3.setDisc("Доступ к архиву");
        d3.setZoneAccessLvl(AccessLevel.LEVEL_3);

        zoneRepository.save(d1);
        zoneRepository.save(d2);
        zoneRepository.save(d3);
        log.info("Seed zones created");
    }

    private void ensureAttempts(User admin, User security, User user) {
        if (accessAttemptRepository.count() != 0) {
            return;
        }

        User adminUser = admin;
        User securityUser = security;
        User normalUser = user;

        Zone zone1 = zoneRepository.findAll().stream()
                .filter(d -> "Серверная".equalsIgnoreCase(d.getName()))
                .findFirst()
                .orElseGet(() -> zoneRepository.findAll().stream().findFirst().orElse(null));

        Zone zone2 = zoneRepository.findAll().stream()
                .filter(d -> "Кладовка".equalsIgnoreCase(d.getName()))
                .findFirst()
                .orElseGet(() -> zoneRepository.findAll().stream().findFirst().orElse(null));

        if (adminUser != null && zone1 != null) {
            AccessAttempt attempt1 = new AccessAttempt();
            attempt1.setUser(adminUser);
            attempt1.setZone(zone1);
            attempt1.setSuccess(true);
            attempt1.setTimestamp(LocalDateTime.of(2025, 2, 11, 12, 45));
            accessAttemptRepository.save(attempt1);
        }

        if (securityUser != null && zone2 != null) {
            AccessAttempt attempt2 = new AccessAttempt();
            attempt2.setUser(securityUser);
            attempt2.setZone(zone2);
            attempt2.setSuccess(true);
            attempt2.setTimestamp(LocalDateTime.of(2025, 2, 11, 12, 45));
            accessAttemptRepository.save(attempt2);
        }

        // Пара попыток "с отказом", чтобы UI не выглядел пустым.
        if (normalUser != null && zone1 != null) {
            AccessAttempt attempt3 = new AccessAttempt();
            attempt3.setUser(normalUser);
            attempt3.setZone(zone1);
            attempt3.setSuccess(false);
            attempt3.setTimestamp(LocalDateTime.of(2025, 2, 11, 12, 46));
            accessAttemptRepository.save(attempt3);
        }

        log.info("Seed access attempts created");
    }
}

