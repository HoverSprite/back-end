package com.example.project.controller;

import com.example.project.model.dto.AuthResponse;
import com.example.project.model.dto.LoginRequest;
import com.example.project.model.dto.RegisterRequest;
import com.example.project.model.entity.Person;
import com.example.project.service.impl.AuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthServiceImpl authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest);
        Person person = authService.getUserDetails(loginRequest);  // Get person details
        String role = String.valueOf(person.getRole());  // Extract role from person
        return ResponseEntity.ok(new AuthResponse(token, role));
    }
}
