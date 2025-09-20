package com.smart.rides.dto;

public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;

    // Constructor with only the relevant fields
    public UserResponse(Long id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Getters only (immutable response)
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}
