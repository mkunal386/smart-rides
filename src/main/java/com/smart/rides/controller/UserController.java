package com.smart.rides.controller;

import com.smart.rides.dto.LoginRequest;
import com.smart.rides.dto.SignupRequest;
import com.smart.rides.dto.UserResponse;
import com.smart.rides.entity.User;
import com.smart.rides.repository.UserRepository;
import com.smart.rides.service.EmailService;
import org.thymeleaf.context.Context;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

        String hashed = passwordEncoder.encode(req.getPassword());
        user.setPasswordHash(hashed);

        User saved = userRepository.save(user);

        // --- HTML EMAIL LOGIC FOR NEW USER ---
        String subject = "Welcome to Smart Rides!";
        Context context = new Context();
        context.setVariable("userName", saved.getName());

        emailService.sendHtmlEmail(saved.getEmail(), subject, "welcome-user", context);
        // --- END OF EMAIL LOGIC ---

        UserResponse resp = new UserResponse(
                saved.getId(), saved.getName(), saved.getEmail(), saved.getPhone()
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
                            user.getId(), user.getName(), user.getEmail(), user.getPhone()
                    );
                    return ResponseEntity.ok(resp);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials"));
    }
}