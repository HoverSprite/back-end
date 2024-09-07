package hoversprite.project.partner;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping // Ensure correct base path if needed
class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping("/persons")
    public List<PersonDTO> getPersonList() {
        return personService.getPersonList();
    }

//    @PostMapping("/signup")
//    public ResponseEntity<String> signup(@RequestBody Person person) {
//        if (personService.isEmailTaken(person.getEmailAddress())) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already taken");
//        }
//        if (personService.isPhoneNumberTaken(person.getPhoneNumber())) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("Phone number already taken");
//        }
//        if (!isValidPhoneNumber(person.getPhoneNumber())) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid phone number format");
//        }
//
//        // Email domain validation for RECEPTIONISTS and SPRAYERS
//        if (("Receptionist".equalsIgnoreCase(String.valueOf(person.getRole())) || "Sprayer".equalsIgnoreCase(String.valueOf(person.getRole())))
//                && !person.getEmailAddress().endsWith("@hoversprite.com")) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email must be under hoversprite.com domain for RECEPTIONISTS and SPRAYERS");
//        }
//
//        // Expertise validation for RECEPTIONISTS and SPRAYERS
//        if (("Receptionist".equalsIgnoreCase(String.valueOf(person.getRole())) || "Sprayer".equalsIgnoreCase(String.valueOf(person.getRole())))
//                && (!"Apprentice".equalsIgnoreCase(String.valueOf(person.getExpertise())) &&
//                !"Adept".equalsIgnoreCase(String.valueOf(person.getExpertise())) &&
//                !"Expert".equalsIgnoreCase(String.valueOf(person.getExpertise())))) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Expertise must be Apprentice, Adept, or Expert");
//        }
//
//        // Password validation: Must contain at least one capital letter and one special character
//        if (!isValidPassword(person.getPasswordHash())) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password must contain at least one capital letter and one special character");
//        }
//
//        // Hash password before saving to database
//        person.setPassword(person.getPasswordHash());
//        personService.addPerson(person);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body("Sign-up successful");
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestParam String emailOrPhone, @RequestParam String password) {
//        if (personService.authenticate(emailOrPhone, password)) {
//            return ResponseEntity.ok("Login successful");
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
//        }
//    }
//
//    private boolean isValidPassword(String password) {
//        Pattern pattern = Pattern.compile("^(?=.*[A-Z])(?=.*[@#$%^&+=]).*$");
//        Matcher matcher = pattern.matcher(password);
//        return matcher.find();
//    }
//
//    private boolean isValidPhoneNumber(String phoneNumber) {
//        // Example pattern: starts with a digit, 10-15 digits long
//        Pattern pattern = Pattern.compile("^\\d{10,15}$");
//        Matcher matcher = pattern.matcher(phoneNumber);
//        return matcher.matches();
//    }
//
//
    @PostMapping("/addPerson")
    public ResponseEntity<String> addPerson(@RequestBody PersonDTO person) {
        personService.addPerson(person);
        return ResponseEntity.status(HttpStatus.CREATED).body("Person added successfully");
    }


    /* // Check conditions for RECEPTIONIST and SPRAYER roles
        if ("Receptionist".equalsIgnoreCase(person.getRole()) || "Sprayer".equalsIgnoreCase(person.getRole())) {
            if (!person.getEmailAddress().endsWith("@hoversprite.com")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email must be under hoversprite.com domain for RECEPTIONISTS and SPRAYERS");
            }

            if (!"Apprentice".equalsIgnoreCase(person.getExpertise()) &&
                    !"Adept".equalsIgnoreCase(person.getExpertise()) &&
                    !"Expert".equalsIgnoreCase(person.getExpertise())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Expertise must be Apprentice, Adept, or Expert");
            }
        }
*/
    /*@PostMapping("/addSamplePersons")
    public ResponseEntity<String> addSamplePersons() {
        Person Farmer = Person.builder()
                .lastName("Nguyen")
                .middleName("Anh")
                .firstName("Huy")
                .phoneNumber("0123456789")
                .emailAddress("huynguyen@gmail.com")
                .homeAddress("12 Nguyen Van Linh, District 7, Ho Chi Minh City")
                .passwordHash("Farmer@123")
                .role("Farmer")
                .build();

        Person Receptionist = Person.builder()
                .lastName("Le")
                .middleName("Thi")
                .firstName("Hoa")
                .phoneNumber("0987654321")
                .emailAddress("haleb@hoversprite.com")
                .homeAddress("34 Vo Van Kiet, District 1, Ho Chi Minh City")
                .passwordHash("Receptionist@123")
                .role("Receptionist")
                .expertise("Adept")
                .build();

        Person Sprayer = Person.builder()
                .lastName("Tran")
                .middleName("Van")
                .firstName("Hung")
                .phoneNumber("0246813579")
                .emailAddress("tran.van.c@hoversprite.com")
                .homeAddress("56 Pham Ngu Lao, District 1, Ho Chi Minh City ")
                .passwordHash("Sprayer@123")
                .role("Sprayer")
                .expertise("Expert")
                .build();

        personService.addPerson(Farmer);
        personService.addPerson(Receptionist);
        personService.addPerson(Sprayer);

        return ResponseEntity.status(HttpStatus.CREATED).body("Sample Persons added successfully");
    }*/
}
