package org.example.acs_v2.repositories;

import org.example.acs_v2.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    List<User> findUsersByUserAccessLvl(int userAccessLvl);
    List<User> findUsersByName(String name);
    User findByName(String name);
    @Query(value = "SELECT u.* FROM users u " +
            "JOIN user_role ur ON u.id = ur.user_id " +
            "WHERE ur.roles = 'ROLE_DIRECTOR' " +
            "AND u.department_id = 5 " +
            "AND u.id <> (SELECT d.head_id FROM departments d WHERE d.id = 5)",
            nativeQuery = true)
    List<User> findDirectorsWithoutDepartment();


}

