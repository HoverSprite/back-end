package hoversprite.project.partner;

import hoversprite.project.common.domain.PersonExpertise;
import hoversprite.project.common.domain.PersonRole;
import hoversprite.project.request.PersonRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }
    @Override
    public List<PersonDTO> getPersonList() {
        return personRepository.findAll().stream().map(PersonMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    @Override
    public PersonDTO addPerson(PersonRequest person) {

//        Map<String, String> validationErrors = validatePersonRequest(person);
//
//        if (!validationErrors.isEmpty()) {
//            throw new ValidationException("Validation failed", validationErrors);
//        }
        Person personToSave = PersonMapper.INSTANCE.toEntity(person);
        return PersonMapper.INSTANCE.toDto(personRepository.save(personToSave));
    }

    public Map<String, String> validatePersonRequest(PersonRequest personRequest) {
        Map<String, String> errors = new HashMap<>();

        if (!isValidFullName(personRequest.getFullName())) {
            errors.put("fullName", "Invalid full name format");
        }
        if (!isValidPhoneNumber(personRequest.getPhoneNumber())) {
            errors.put("phoneNumber", "Invalid phone number format");
        }
        if (!isValidEmailAddress(personRequest.getEmailAddress())) {
            errors.put("emailAddress", "Invalid email format");
        }
        if (personRequest.getPasswordHash() != null && !isValidPassword(personRequest.getPasswordHash())) {
            errors.put("password", "Password must contain at least one capital letter and one special character");
        }
        if (!personRequest.getEmailAddress().endsWith("@hoversprite.com") && (personRequest.getRole() == PersonRole.RECEPTIONIST ||
                personRequest.getRole() == PersonRole.SPRAYER)) {
            errors.put("emailAddress", "Email must be a @hoversprite.com domain");
        }

        return errors;
    }

    private boolean isValidFullName(String fullName) {
        String fullNameRegex = "^[A-ZÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴĐÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸ]" +
                "[a-zàáạảãâầấậẩẫăằắặẳẵđèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹ]*" +
                "(?:[A-Z][a-zàáạảãâầấậẩẫăằắặẳẵđèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹ]*)*" +  // allows second capital letter
                "(?:[ ][A-ZÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴĐÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸ]" +
                "[a-zàáạảãâầấậẩẫăằắặẳẵđèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹ]*)*$";
        return Pattern.matches(fullNameRegex, fullName);
    }

    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[A-Z])(?=.*[!@#$%^&*()])(?=.*[0-9])(?=.*[a-z]).{8,}$";
        return Pattern.matches(passwordRegex, password);
    }


    @Override
    public Optional<PersonDTO> loadUserByUsername(String username) {
        return personRepository.findByEmailAddress(username).map(PersonMapper.INSTANCE::toDto);
    }

    @Override
    public boolean existsByEmail(String emailAddress) {
        return personRepository.existsByEmail(emailAddress);
    }

    @Override
    public PersonDTO updatePerson(PersonDTO personDTO) {
        Person person = personRepository.findById(personDTO.getId())
                .orElseThrow(() -> new RuntimeException("The person cannot be found to update."));
        person.setOauthProvider(personDTO.getOauthProvider());
        return PersonMapper.INSTANCE.toDto(personRepository.save(person));
    }

    @Override
    public boolean isEmailTaken(String emailAddress) {
        return personRepository.findAll().stream()
                .anyMatch(person -> person.getEmailAddress().equalsIgnoreCase(emailAddress));
    }

    @Override
    public boolean isPhoneNumberTaken(String phoneNumber) {
        return personRepository.findAll().stream()
                .anyMatch(person -> person.getPhoneNumber().equals(phoneNumber));
    }

    @Override
    public boolean authenticate(String emailOrPhone, String password) {
        Optional<Person> person = personRepository.findAll().stream()
                .filter(p -> p.getEmailAddress().equalsIgnoreCase(emailOrPhone) || p.getPhoneNumber().equals(emailOrPhone))
                .findFirst();

        return person.isPresent() && person.get().getPasswordHash().equals(password);
    }

    @Override
    public boolean isValidPhoneNumber(String phoneNumber) {
        String phonePattern = "^(\\+84|0)[3|5|7|8|9][0-9]{8}$";
        return Pattern.matches(phonePattern, phoneNumber);
    }

    @Override
    public PersonDTO findFarmerById(Long farmerId) {
        Optional<Person> farmerOptional = personRepository.findById(farmerId);

        if (farmerOptional.isPresent()) {
            Person farmer = farmerOptional.get();

            // Check if the person is actually a farmer
            if (farmer.getRole() == PersonRole.FARMER) {
                return PersonMapper.INSTANCE.toDto(farmer);
            }
        }

        // Return null if no farmer is found or if the person is not a farmer
        return null;
    }

    @Override
    public boolean isValidEmailAddress(String emailAddress) {
        return true;
    }

    @Override
    public List<PersonDTO> getUserByIds(List<Long> ids) {
        return personRepository.getUsersById(ids).stream().map(PersonMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    @Override
    public PersonDTO findById(Long id) {
        return PersonMapper.INSTANCE.toDto(personRepository.findById(id).orElse(null));
    }

    @Override
    public Map<PersonExpertise, List<PersonDTO>> getSprayersGroupedByExpertise(List<Long> excludedIds) {
        List<PersonDTO> personDTOS = personRepository.getSprayersThatNotExcluded(excludedIds)
                .stream().map(PersonMapper.INSTANCE::toDto).collect(Collectors.toList());

        return personDTOS.stream()
                .collect(Collectors.groupingBy(PersonDTO::getExpertise));
    }

    @Override
    public PersonDTO findFarmerByPhoneNumber(String phoneNumber) {
        Optional<Person> person = personRepository.findByPhoneNumberAndRole(phoneNumber, PersonRole.FARMER);
        return person.map(PersonMapper.INSTANCE::toDto).orElse(null);
    }

    @Override
    public Optional<PersonDTO> findByEmailAddress(String username) {
        return loadUserByUsername(username);
    }
    @Override
    public PersonDTO createFarmer(PersonRequest personRequest) {
        // Validate the phone number
        if (!isValidPhoneNumber(personRequest.getPhoneNumber())) {
            throw new IllegalArgumentException("Invalid phone number");
        }

        // Check if the phone number is already taken
        if (isPhoneNumberTaken(personRequest.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number is already in use");
        }

        // Set the role to FARMER
        personRequest.setRole(PersonRole.FARMER);

        // Create and save the new farmer
        Person newFarmer = PersonMapper.INSTANCE.toEntity(personRequest);
        Person savedFarmer = personRepository.save(newFarmer);

        return PersonMapper.INSTANCE.toDto(savedFarmer);
    }

}
