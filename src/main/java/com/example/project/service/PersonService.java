package com.example.project.service;

import com.example.project.model.entity.Person;

import java.util.List;

public interface PersonService {

    public List<Person> getPersonList();

    public void addPerson();
}
