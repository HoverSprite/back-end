package hoversprite.project.onetimecode;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface OneTimeCodeRepository extends JpaRepository<OneTimeCode, Long>, OneTimeCodeRepositoryCustom {
    // Method to check existence of email and phone number
}
