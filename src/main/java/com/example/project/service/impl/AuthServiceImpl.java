package com.example.project.service.impl;

import com.example.project.model.dto.LoginRequest;
import com.example.project.model.dto.RegisterRequest;
import com.example.project.model.entity.Person;
import com.example.project.repository.PersonRepository;
import com.example.project.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    public void register(RegisterRequest registerRequest) {
        // Check if the email or phone already exists
        if (personRepository.existsByEmailAddress(registerRequest.getEmailAddress()) ||
                personRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())) {
            throw new IllegalArgumentException("Email or phone number already exists.");
        }

        // Create a new Person entity
        Person person = new Person();
        person.setLastName(registerRequest.getLastName());
        person.setMiddleName(registerRequest.getMiddleName());
        person.setFirstName(registerRequest.getFirstName());
        person.setPhoneNumber(registerRequest.getPhoneNumber());
        person.setEmailAddress(registerRequest.getEmailAddress());
        person.setHomeAddress(registerRequest.getHomeAddress());
        person.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));  // Encode password
        person.setRole(Person.Role.valueOf(registerRequest.getRole()));
        person.setExpertise(Person.Expertise.valueOf(registerRequest.getExpertise()));

        // Save the new user to the database
        personRepository.save(person);
    }

    public String login(LoginRequest loginRequest) {
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmailOrPhone(), loginRequest.getPassword())
        );

        // Generate and return JWT token
        return jwtUtil.generateToken(loginRequest.getEmailOrPhone());
    }

    public Person getUserDetails(LoginRequest loginRequest) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmailOrPhone(), loginRequest.getPassword())
        );

        // Find the user by email or phone
        String emailOrPhone = loginRequest.getEmailOrPhone();
        Person person = personRepository.findByEmailAddress(emailOrPhone)
                .orElseGet(() -> personRepository.findByPhoneNumber(emailOrPhone)
                        .orElseThrow(() -> new IllegalArgumentException("User not found")));
        return person;
    }
}
