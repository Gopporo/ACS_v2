package org.example.acs_v2.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.exceptions.ResourceNotFoundException;
import org.example.acs_v2.models.Department;
import org.example.acs_v2.models.User;
import org.example.acs_v2.repositories.DepartmentRepository;
import org.example.acs_v2.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * Сервис для управления отделами
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    /**
     * Создает новый отдел с назначением главы
     *
     * @param department отдел для создания
     * @param headId     ID главы отдела
     * @return true если отдел создан, false если отдел с таким именем уже существует
     */
    public boolean createDepartment(Department department, Long headId) {
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

        return true;
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
