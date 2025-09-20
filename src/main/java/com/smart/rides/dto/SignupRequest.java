package com.smart.rides.dto;

import jakarta.validation.constraints.*;

public class SignupRequest {

    @NotBlank
    @Size(min = 2, max = 60)
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 10, max = 20)
    private String phone;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;   // will be hashed before saving

    // Removed role, vehicleModel, licensePlate, and capacity

    // getters/setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
