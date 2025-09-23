package com.smart.rides.controller;

import com.smart.rides.dto.LoginRequest;
import com.smart.rides.dto.SignupRequest;
import com.smart.rides.dto.UserResponse;
import com.smart.rides.entity.User;
import com.smart.rides.entity.Role;
import com.smart.rides.repository.UserRepository;
import com.smart.rides.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.List;
import org.thymeleaf.context.Context;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000"})
public class UserController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final EmailService emailService;

    public UserController(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody SignupRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already registered");
        }

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setRole(Role.PASSENGER);

        String hashed = passwordEncoder.encode(req.getPassword());
        user.setPasswordHash(hashed);

        User saved = userRepository.save(user);

        String subject = "Welcome to Smart Rides!";
        Context context = new Context();
        context.setVariable("userName", saved.getName());

        emailService.sendHtmlEmail(saved.getEmail(), subject, "welcome-user", context);

        UserResponse resp = new UserResponse(
                saved.getId(), saved.getName(), saved.getEmail(), saved.getPhone(), saved.getRole()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        return userRepository.findByEmail(req.getEmail())
                .map(user -> {
                    boolean ok = passwordEncoder.matches(req.getPassword(), user.getPasswordHash());
                    if (!ok) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");

                    UserResponse resp = new UserResponse(
                            user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getRole()
                    );
                    return ResponseEntity.ok(resp);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials"));
    }

    @GetMapping("/admin/users/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/admin/users/{userId}/block")
    public ResponseEntity<String> toggleUserBlockStatus(@PathVariable Long userId, @RequestParam boolean isBlocked) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setIsBlocked(isBlocked);
                    userRepository.save(user);
                    return ResponseEntity.ok("User block status updated.");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found."));
    }

    @PutMapping("/admin/users/{userId}/verify")
    public ResponseEntity<String> toggleDriverVerification(@PathVariable Long userId, @RequestParam boolean isVerified) {
        return userRepository.findById(userId)
                .map(user -> {
                    if (user.getRole() != Role.DRIVER) { // --- This is the change ---
                        return ResponseEntity.badRequest().body("User is not a DRIVER and cannot be verified.");
                    }
                    user.setIsVerified(isVerified);
                    userRepository.save(user);
                    return ResponseEntity.ok("Driver verification status updated.");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found."));
    }

    @PostMapping("/user/{userId}/update-role")
    public ResponseEntity<String> updateUserRole(@PathVariable Long userId, @RequestParam Role newRole) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setRole(newRole);
                    userRepository.save(user);
                    return ResponseEntity.ok("User role updated successfully.");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found."));
    }
}