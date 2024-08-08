package com.example.project.service.impl;

import com.example.project.model.entity.Person;
import com.example.project.repository.PersonRepository;
import com.example.project.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonRepository personRepository;
    @Override
    public List<Person> getPersonList() {
        return personRepository.findAll();
    }

    @Override
    public void addPerson() {
        personRepository.save(Person.builder().name("Huy Anh").build());
    }
}
