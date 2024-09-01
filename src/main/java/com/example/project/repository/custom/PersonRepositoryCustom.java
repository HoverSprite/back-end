package com.example.project.repository.custom;

import com.example.project.model.entity.Person;

import java.util.List;

public interface PersonRepositoryCustom {
    List<Person> getUsersById(List<Integer> ids);
}
