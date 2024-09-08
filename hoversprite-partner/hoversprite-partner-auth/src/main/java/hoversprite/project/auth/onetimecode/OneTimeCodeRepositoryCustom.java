package hoversprite.project.auth.onetimecode;


import java.util.List;
import java.util.Optional;

interface OneTimeCodeRepositoryCustom {
    boolean otpExistedForUser(Long userId);

    Optional<OneTimeCode> findByUserId(Long userId);
}
