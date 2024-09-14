package hoversprite.project.response;

import hoversprite.project.common.domain.PersonExpertise;
import hoversprite.project.common.domain.PersonRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonResponse {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String homeAddress;
    private String emailAddress;
    private PersonRole role;
    private PersonExpertise expertise;
    private String profilePictureUrl;
}