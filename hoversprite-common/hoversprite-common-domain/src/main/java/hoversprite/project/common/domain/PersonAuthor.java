package hoversprite.project.common.domain;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonAuthor {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String homeAddress;
    private String emailAddress;
    private String passwordHash;
    private String oauthProvider;
    private PersonRole role;
    private PersonExpertise expertise;
    private String profilePictureUrl;
}
