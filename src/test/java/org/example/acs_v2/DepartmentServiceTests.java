package org.example.acs_v2;

import org.example.acs_v2.models.Department;
import org.example.acs_v2.models.Role;
import org.example.acs_v2.models.User;
import org.example.acs_v2.repositories.DepartmentRepository;
import org.example.acs_v2.repositories.UserRepository;
import org.example.acs_v2.services.DepartmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTests {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DepartmentService departmentService;

    @Test
    void updateDepartmentWithEmployees_movesUnselectedEmployeesToNoDepartment() {
        Department noDepartment = new Department();
        noDepartment.setId(1L);
        noDepartment.setName("Без отдела");

        Department targetDepartment = new Department();
        targetDepartment.setId(7L);
        targetDepartment.setName("IT");

        User director = new User();
        director.setId(10L);
        director.setRoles(Set.of(Role.ROLE_DIRECTOR));

        User keptEmployee = new User();
        keptEmployee.setId(21L);
        keptEmployee.setDepartment(targetDepartment);

        User removedEmployee = new User();
        removedEmployee.setId(22L);
        removedEmployee.setDepartment(targetDepartment);

        when(departmentRepository.findById(7L)).thenReturn(Optional.of(targetDepartment));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(noDepartment));
        when(userRepository.findById(10L)).thenReturn(Optional.of(director));
        when(userRepository.findByDepartmentIdAndApprovedTrue(7L)).thenReturn(List.of(keptEmployee, removedEmployee));
        when(userRepository.findAllById(anyIterable())).thenReturn(List.of(keptEmployee));

        departmentService.updateDepartmentWithEmployees(7L, "R&D", 10L, List.of(21L));

        verify(departmentRepository).save(targetDepartment);
        verify(userRepository, atLeastOnce()).saveAll(any());
        assertEquals("R&D", targetDepartment.getName());
        assertEquals(noDepartment, removedEmployee.getDepartment());
    }
}
