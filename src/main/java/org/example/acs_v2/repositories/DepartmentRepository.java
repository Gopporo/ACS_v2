package org.example.acs_v2.repositories;

import org.example.acs_v2.models.Department;
import org.example.acs_v2.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Department findByName(String name);
    List<Department> findDepartmentsByName(String name);
}
