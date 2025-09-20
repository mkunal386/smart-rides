package com.smart.rides.controller;

import com.smart.rides.dto.LoginRequest;
import com.smart.rides.dto.SignupRequest;
import com.smart.rides.dto.UserResponse;
import com.smart.rides.entity.User;
import com.smart.rides.repository.UserRepository;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000"}) // frontend URL
public class UserController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody SignupRequest req) {
        // Check if the email is already registered
        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already registered");
        }

        // Create new User object and set its details
        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());

        // Hash the password and set it
        String hashed = passwordEncoder.encode(req.getPassword());
        user.setPasswordHash(hashed);

        // Save the new user
        User saved = userRepository.save(user);

        // Create response object
        UserResponse resp = new UserResponse(
                saved.getId(), saved.getName(), saved.getEmail(), saved.getPhone()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        return userRepository.findByEmail(req.getEmail())
                .map(user -> {
                    // Check if the password matches
                    boolean ok = passwordEncoder.matches(req.getPassword(), user.getPasswordHash());
                    if (!ok) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");

                    // Create response object with user details
                    UserResponse resp = new UserResponse(
                            user.getId(), user.getName(), user.getEmail(), user.getPhone()
                    );
                    return ResponseEntity.ok(resp);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials"));
    }
}
