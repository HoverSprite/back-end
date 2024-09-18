package hoversprite.project.partner;

import com.mysema.commons.lang.Pair;
import hoversprite.project.common.domain.PersonExpertise;
import hoversprite.project.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class PersonController {



    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/user-id")
    public ResponseEntity<?> getUserIdByEmail(@RequestParam String email) {
        Optional<PersonDTO> personOpt = personService.findByEmailAddress(email);
        if (personOpt.isPresent()) {
            PersonDTO person = personOpt.get();
            return ResponseEntity.ok(Map.of("id", person.getId(), "role", person.getRole()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @GetMapping("/userName")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'FARMER', 'SPRAYER')")
    public ResponseEntity<String> getUserName() {
        String userName = SecurityUtils.getCurrentUserName();
        return ResponseEntity.ok(userName);
    }

}
