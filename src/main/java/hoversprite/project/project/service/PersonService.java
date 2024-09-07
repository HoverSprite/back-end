package hoversprite.project.project.service;

import hoversprite.project.project.model.entity.Person;

import java.util.List;

public interface PersonService {

    List<Person> getPersonList();

    void addPerson(Person person);

    boolean isEmailTaken(String emailAddress);

    boolean isPhoneNumberTaken(String phoneNumber);

    boolean authenticate(String emailOrPhone, String password);

    boolean isValidPhoneNumber(String phoneNumber);
    boolean isValidEmailAddress(String emailAddress);

    List<Person> getUserByIds(List<Integer> ids);
}