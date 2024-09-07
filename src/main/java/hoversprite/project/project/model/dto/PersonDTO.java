package hoversprite.project.project.model.dto;

import hoversprite.project.project.model.entity.PersonRole;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonDTO {
    private Integer id;
    private String lastName;
    private String middleName;
    private String firstName;
    private String phoneNumber;
    private String homeAddress;
    private String emailAddress;
    private PersonRole role;
    private PersonExpertise expertise;
    private String profilePictureUrl;
}