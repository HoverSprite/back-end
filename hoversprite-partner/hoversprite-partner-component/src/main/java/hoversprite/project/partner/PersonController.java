package hoversprite.project.partner;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
