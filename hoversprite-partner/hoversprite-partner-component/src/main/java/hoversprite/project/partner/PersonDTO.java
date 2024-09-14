package hoversprite.project.partner;

import hoversprite.project.common.domain.PersonExpertise;
import hoversprite.project.common.domain.PersonRole;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonDTO {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String homeAddress;
    private String emailAddress;
    private String passwordHash;
    private PersonRole role;
    private PersonExpertise expertise;
    private String profilePictureUrl;
}