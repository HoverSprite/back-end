package hoversprite.project.partner;

import hoversprite.project.common.domain.PersonExpertise;
import hoversprite.project.common.domain.PersonRole;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "PERSON")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
class Person {

    @Id
    @Column(name = "PERSON_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "MIDDLE_NAME")
    private String middleName;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "HOME_ADDRESS")
    private String homeAddress;

    @Column(name = "EMAIL_ADDRESS")
    private String emailAddress;

    @Column(name = "PASSWORD_HASH")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE")
    private PersonRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "EXPERTISE")
    private PersonExpertise expertise;

    @Column(name = "PROFILE_PICTURE_URL")
    private String profilePictureUrl;

    //MODIFY

    @PrePersist
    public void setDefaultProfilePicture() {
        if (this.profilePictureUrl == null || this.profilePictureUrl.isEmpty()) {
            this.profilePictureUrl = getDefaultAvatarUrlForRole(this.role);
        }
    }

    // Set password with hashing
    public void setPassword(String password) {
        this.passwordHash = Integer.toString(password.hashCode());
    }

    private String getDefaultAvatarUrlForRole(PersonRole role) {
        // Handle invalid role
        return switch (role) {
            case ADMIN -> "admin-avatar-url";
            case FARMER -> "farmer-avatar-url";
            case RECEPTIONIST -> "receptionist-avatar-url";
            case SPRAYER -> "sprayer-avatar-url";
        };
    }
}
