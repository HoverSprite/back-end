package com.example.project.model.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String lastName;
    private String middleName;
    private String firstName;
    private String phoneNumber;
    private String emailAddress;
    private String homeAddress;
    private String password;
    private String role;
    private String expertise;
}
