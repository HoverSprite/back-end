package hoversprite.project.partner;

import hoversprite.project.common.domain.PersonExpertise;
import hoversprite.project.common.domain.PersonRole;
import hoversprite.project.request.PersonRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
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
        Person personToSave = PersonMapper.INSTANCE.toEntity(person);
        return PersonMapper.INSTANCE.toDto(personRepository.save(personToSave));
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


}
