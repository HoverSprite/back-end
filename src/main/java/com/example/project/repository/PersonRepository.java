package com.example.project.repository;

import com.example.project.model.entity.Person;
import com.example.project.repository.custom.PersonRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long>, PersonRepositoryCustom {
    // Method to check existence of email and phone number
    boolean existsByEmailAddress(String emailAddress);
    boolean existsByPhoneNumber(String phoneNumber);

    // Method to search by email or phone number
    Optional<Person> findByEmailAddress(String emailAddress);
    Optional<Person> findByPhoneNumber(String phoneNumber);
}
