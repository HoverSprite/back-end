//package com.example.project.security;
//
//import com.example.project.model.entity.Person;
//import com.example.project.repository.PersonRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Optional;
//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//
//    @Autowired
//    private PersonRepository personRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return loadUserByEmailOrPhone(username);
//    }
//
//    public UserDetails loadUserByEmailOrPhone(String identifier) throws UsernameNotFoundException {
//        // Attempt to find the user by email
//        Optional<Person> personOpt = personRepository.findByEmailAddress(identifier);
//
//        // If not found by email, attempt to find by phone number
//        if (!personOpt.isPresent()) {
//            personOpt = personRepository.findByPhoneNumber(identifier);
//        }
//
//        // If still not found, throw UsernameNotFoundException
//        Person person = personOpt.orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        // Convert person role to GrantedAuthority
//        Collection<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(person.getRole().name()));
//
//        return new org.springframework.security.core.userdetails.User(person.getEmailAddress(), person.getPasswordHash(), authorities);
//    }
//}
