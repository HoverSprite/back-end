package hoversprite.project.partner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface PersonRepository extends JpaRepository<Person, Long>, PersonRepositoryCustom {
    // Method to check existence of email and phone number
    boolean existsByEmailAddress(String emailAddress);
    boolean existsByPhoneNumber(String phoneNumber);

    // Method to search by email or phone number
    Optional<Person> findByEmailAddress(String emailAddress);
    Optional<Person> findByPhoneNumber(String phoneNumber);
}
