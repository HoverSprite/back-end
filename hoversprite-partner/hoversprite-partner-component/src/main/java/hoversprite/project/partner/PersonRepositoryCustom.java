package hoversprite.project.partner;


import java.util.List;

interface PersonRepositoryCustom {
    List<Person> getUsersById(List<Long> ids);
}
