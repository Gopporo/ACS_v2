package org.example.acs_v2.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.models.Department;
import org.example.acs_v2.models.User;
import org.example.acs_v2.models.Zone;
import org.example.acs_v2.models.enums.Role;
import org.example.acs_v2.repositories.DepartmentRepository;
import org.example.acs_v2.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DepartmentService {
    @Autowired
    private final DepartmentRepository departmentRepository;
    @Autowired
    private final UserRepository userRepository;

    public boolean createDepartment(Department department, Long headId) {
        System.out.println("Сервис для создания отдела вызвал");
        String departmentName = department.getName();

        // Проверяем, существует ли отдел с таким именем
        if (departmentRepository.findByName(departmentName) != null) {
            return false;
        }
        System.out.println("Название проверил");

        // Сохраняем отдел
        log.info("Saving new Department with name: {}", departmentName);
        departmentRepository.save(department);

        // Назначаем главу отдела
        if (headId != null) {
            User head = userRepository.findById(headId).orElseThrow(() ->
                    new RuntimeException("User with ID " + headId + " not found"));

            // Устанавливаем связь
            head.setDepartment(department);
            department.setHead(head);

            // Сохраняем изменения
            userRepository.save(head);
            departmentRepository.save(department);
        }

        System.out.println("Отдел и глава отдела сохранены");
        return true;
    }

    public List<Department> list() {
        return departmentRepository.findAll();
    }

    public List<Department> getDepartmentByName(String name) {
        return departmentRepository.findDepartmentsByName(name);
    }
}
