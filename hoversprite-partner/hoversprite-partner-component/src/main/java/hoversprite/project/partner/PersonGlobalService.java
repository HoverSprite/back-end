package hoversprite.project.partner;

import hoversprite.project.common.domain.PersonExpertise;

import java.util.List;
import java.util.Map;

public interface PersonGlobalService {

    List<PersonDTO> getPersonList();

    boolean isEmailTaken(String emailAddress);

    boolean isPhoneNumberTaken(String phoneNumber);

    boolean authenticate(String emailOrPhone, String password);

    boolean isValidPhoneNumber(String phoneNumber);
    boolean isValidEmailAddress(String emailAddress);
    PersonDTO findFarmerById(Long farmerId);

    List<PersonDTO> getUserByIds(List<Long> ids);

    Map<PersonExpertise, List<PersonDTO>> getSprayersGroupedByExpertise(List<Long> excludedIds);

    PersonDTO findFarmerByPhoneNumber(String phoneNumber);
}
