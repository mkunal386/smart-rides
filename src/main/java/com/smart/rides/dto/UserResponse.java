package com.smart.rides.dto;

import com.smart.rides.entity.Role; // --- Change: New import for Role enum ---

public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Role role; // --- New field ---

    // Constructor with only the relevant fields
    public UserResponse(Long id, String name, String email, String phone, Role role) { // --- Change: Added Role parameter ---
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role; // --- Change: Set the new field ---
    }

    // Getters only (immutable response)
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Role getRole() { return role; } // --- New getter ---
}