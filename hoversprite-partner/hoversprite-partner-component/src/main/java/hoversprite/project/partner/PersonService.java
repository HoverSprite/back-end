package hoversprite.project.partner;


import hoversprite.project.request.PersonRequest;

import java.util.List;
import java.util.Optional;

public interface PersonService extends PersonGlobalService {

    Optional<PersonDTO> loadUserByUsername(String username);

    boolean existsByEmail(String emailAddress);

    PersonDTO updatePerson(PersonDTO personDTO);

    PersonDTO createFarmer(PersonRequest personRequest);
}