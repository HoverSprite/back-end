package com.example.project.service.impl;

import com.example.project.model.entity.Person;
import com.example.project.repository.PersonRepository;
import com.example.project.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Override
    public List<Person> getPersonList() {
        return personRepository.findAll();
    }

    @Override
    public void addPerson(Person person) {
        personRepository.save(person);
    }

    @Override
    public boolean isEmailTaken(String emailAddress) {
        return personRepository.findAll().stream()
                .anyMatch(person -> person.getEmailAddress().equalsIgnoreCase(emailAddress));
    }

    @Override
    public boolean isPhoneNumberTaken(String phoneNumber) {
        return personRepository.findAll().stream()
                .anyMatch(person -> person.getPhoneNumber().equals(phoneNumber));
    }

    @Override
    public boolean authenticate(String emailOrPhone, String password) {
        Optional<Person> person = personRepository.findAll().stream()
                .filter(p -> p.getEmailAddress().equalsIgnoreCase(emailOrPhone) || p.getPhoneNumber().equals(emailOrPhone))
                .findFirst();

        return person.isPresent() && person.get().getPasswordHash().equals(password);
    }

    @Override
    public boolean isValidPhoneNumber(String phoneNumber) {
        String phonePattern = "^(\\+84|0)[3|5|7|8|9][0-9]{8}$";
        return Pattern.matches(phonePattern, phoneNumber);
    }

    @Override
    public boolean isValidEmailAddress(String emailAddress) {
        return true;
    }

    @Override
    public List<Person> getUserByIds(List<Integer> ids) {
        return personRepository.getUsersById(ids);
    }


}
