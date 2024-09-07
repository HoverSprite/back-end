package hoversprite.project.partner;

import java.util.List;

public interface PersonGlobalService {

    List<PersonDTO> getPersonList();

    boolean isEmailTaken(String emailAddress);

    boolean isPhoneNumberTaken(String phoneNumber);

    boolean authenticate(String emailOrPhone, String password);

    boolean isValidPhoneNumber(String phoneNumber);
    boolean isValidEmailAddress(String emailAddress);

    List<PersonDTO> getUserByIds(List<Long> ids);
}
