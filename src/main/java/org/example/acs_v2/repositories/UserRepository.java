package org.example.acs_v2.repositories;

import org.example.acs_v2.models.User;
import org.example.acs_v2.models.enums.AccessLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByFingerprintHash(String fingerprintHash);
    List<User> findUsersByUserAccessLvl(AccessLevel userAccessLvl);

    @Query("SELECT u FROM User u WHERE LOWER(CONCAT(COALESCE(u.lastName, ''), ' ', COALESCE(u.firstName, ''), ' ', COALESCE(u.surname, ''))) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findUsersByName(String name);

    @Query("SELECT u FROM User u WHERE LOWER(CONCAT(COALESCE(u.lastName, ''), ' ', COALESCE(u.firstName, ''), ' ', COALESCE(u.surname, ''))) = LOWER(:name)")
    User findByName(String name);
    @Query(value = "SELECT u.* FROM users u " +
            "JOIN user_role ur ON u.id = ur.user_id " +
            "WHERE ur.roles = 'ROLE_DIRECTOR' " +
            "AND (u.department_id IS NULL OR u.department_id = 1)",
            nativeQuery = true)
    List<User> findDirectorsWithoutDepartment();
    List<User> findByApproved(boolean approved);
    @Query("SELECT u FROM User u WHERE u.approved = true AND (u.department IS NULL OR u.department.id = 1)")
    List<User> findByWithoutDepartmentAndApprovedTrue();
    List<User> findByDepartmentIdAndApprovedTrue(Long departmentId);


}

