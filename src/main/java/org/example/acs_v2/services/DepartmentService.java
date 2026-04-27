package org.example.acs_v2.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.exceptions.ResourceNotFoundException;
import org.example.acs_v2.models.Department;
import org.example.acs_v2.models.User;
import org.example.acs_v2.models.Role;
import org.example.acs_v2.repositories.DepartmentRepository;
import org.example.acs_v2.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * Сервис для управления отделами
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DepartmentService {
    public static final String NO_DEPARTMENT_NAME = "Без отдела";

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    private Department getNoDepartment() {
        Department none = departmentRepository.findFirstByNameOrderByIdAsc(NO_DEPARTMENT_NAME);
        if (none != null) {
            return none;
        }
        // fallback: historical assumption, if someone preserved id=1
        return departmentRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("Department", 1L));
    }

    /**
     * Создает новый отдел с назначением главы
     *
     * @param department отдел для создания
     * @param headId     ID главы отдела
     * @return true если отдел создан, false если отдел с таким именем уже существует
     */
    public boolean createDepartment(Department department, Long headId, List<Long> employeeIds) {
        log.debug("Creating new department with name: {}", department.getName());
        String departmentName = department.getName();

        // Проверяем, существует ли отдел с таким именем
        if (departmentRepository.findByName(departmentName) != null) {
            log.warn("Department with name '{}' already exists", departmentName);
            return false;
        }
        log.debug("Department name '{}' is available", departmentName);

        // Сохраняем отдел
        log.info("Saving new Department with name: {}", departmentName);
        departmentRepository.save(department);

        // Назначаем главу отдела
        if (headId != null) {
            User head = userRepository.findById(headId).orElseThrow(() ->
                    new ResourceNotFoundException("User", headId));

            boolean isDirector = head.getRoles().stream().anyMatch(role -> role == Role.ROLE_DIRECTOR);
            if (!isDirector) {
                throw new IllegalArgumentException("Руководитель должен иметь роль директора");
            }

            // Устанавливаем связь
            head.setDepartment(department);
            department.setHead(head);

            // Сохраняем изменения
            userRepository.save(head);
            departmentRepository.save(department);
            log.info("Department '{}' created with head user ID: {}", departmentName, headId);
        } else {
            log.info("Department '{}' created without head", departmentName);
        }

        if (employeeIds != null && !employeeIds.isEmpty()) {
            List<User> employees = userRepository.findAllById(employeeIds);
            for (User employee : employees) {
                employee.setDepartment(department);
            }
            userRepository.saveAll(employees);
            log.info("Assigned {} employees to department '{}'", employees.size(), departmentName);
        }

        return true;
    }

    public void updateDepartmentWithEmployees(Long departmentId,
                                              String name,
                                              Long headId,
                                              List<Long> employeeIds) {
        Department department = getDepartmentById(departmentId);
        Department noDepartment = getNoDepartment();

        department.setName(name);
        if (headId != null) {
            User head = userRepository.findById(headId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", headId));
            boolean isDirector = head.getRoles().stream().anyMatch(role -> role == Role.ROLE_DIRECTOR);
            if (!isDirector) {
                throw new IllegalArgumentException("Руководитель должен иметь роль директора");
            }
            department.setHead(head);
            head.setDepartment(department);
            userRepository.save(head);
        } else {
            department.setHead(null);
        }

        List<User> currentEmployees = userRepository.findByDepartmentIdAndApprovedTrue(departmentId);
        Set<Long> selectedEmployeeIds = employeeIds == null ? Set.of() : new HashSet<>(employeeIds);

        for (User employee : currentEmployees) {
            boolean keepInDepartment = selectedEmployeeIds.contains(employee.getId())
                    || (department.getHead() != null && employee.getId().equals(department.getHead().getId()));
            if (!keepInDepartment) {
                employee.setDepartment(noDepartment);
            }
        }

        if (!selectedEmployeeIds.isEmpty()) {
            List<User> selectedEmployees = userRepository.findAllById(selectedEmployeeIds);
            for (User employee : selectedEmployees) {
                employee.setDepartment(department);
            }
            userRepository.saveAll(selectedEmployees);
        }

        userRepository.saveAll(currentEmployees);
        departmentRepository.save(department);
    }

    /**
     * Получает список всех отделов
     *
     * @return список отделов
     */
    public List<Department> list() {
        log.debug("Getting all departments");
        return departmentRepository.findAll();
    }

    /**
     * Получает отделы по имени
     *
     * @param name имя отдела для поиска
     * @return список отделов с указанным именем
     */
    public List<Department> getDepartmentByName(String name) {
        log.debug("Getting departments by name: {}", name);
        return departmentRepository.findDepartmentsByName(name);
    }

    /**
     * Получает отдел по ID
     *
     * @param departmentId ID отдела
     * @return отдел
     * @throws ResourceNotFoundException если отдел не найден
     */
    public Department getDepartmentById(Long departmentId) {
        log.debug("Getting department by ID: {}", departmentId);
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department", departmentId));
    }

    public Department save(Department department) {
        return departmentRepository.save(department);
    }

    public void deleteById(Long departmentId) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException("Department", departmentId);
        }
        departmentRepository.deleteById(departmentId);
    }

}
