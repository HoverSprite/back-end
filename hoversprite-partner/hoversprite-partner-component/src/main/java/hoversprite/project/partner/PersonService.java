package hoversprite.project.partner;


import hoversprite.project.request.PersonRequest;

import java.util.List;
import java.util.Optional;

interface PersonService extends PersonGlobalService {
    PersonDTO addPerson(PersonRequest person);

    Optional<PersonDTO> loadUserByUsername(String username);

    boolean existsByEmail(String emailAddress);
}