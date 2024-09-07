package hoversprite.project.project.repository.custom;

import hoversprite.project.project.model.entity.Person;

import java.util.List;

public interface PersonRepositoryCustom {
    List<Person> getUsersById(List<Integer> ids);
}
