package hoversprite.project.onetimecode;


import java.util.Optional;

interface OneTimeCodeRepositoryCustom {
    boolean otpExistedForUser(Long userId);

    Optional<OneTimeCode> findByUserId(Long userId);
}
