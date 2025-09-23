package com.smart.rides.repository;

import com.smart.rides.entity.User;
import com.smart.rides.entity.Role; // --- Change: New import for the Role enum ---
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // check if a user exists by email
    boolean existsByEmail(String email);

    // find a user by email
    Optional<User> findByEmail(String email);

    // --- Change: New method for Admin Dashboard ---
    // Counts the number of users by their role
    long countByRole(Role role);
}