//package com.example.project.controller;
//
//
//import com.example.project.model.entity.Person;
//import com.example.project.service.PersonService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@CrossOrigin
//public class PersonController {
//
//    @Autowired
//    private PersonService personService;
//
//    @GetMapping("/persons")
//    public List<Person> getPersonList() {
//        return personService.getPersonList();
//    }
//
//    @PostMapping("/addPerson")
//    public void addPerson() {
//        personService.addPerson();
//    }
//}
