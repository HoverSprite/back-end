package com.example.project.model.entity;

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
public class Person {

    @Id
    @Column(name = "PERSON_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;

    @Column(name = "MIDDLE_NAME")
    private String middleName;

    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @Column(name = "PHONE_NUMBER", nullable = false)
    private String phoneNumber;

    @Column(name = "HOME_ADDRESS")
    private String homeAddress;

    @Column(name = "EMAIL_ADDRESS", nullable = false)
    private String emailAddress;

    @Column(name = "PASSWORD_HASH", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "EXPERTISE")
    private Expertise expertise;

    @Column(name = "PROFILE_PICTURE_URL")
    private String profilePictureUrl;

    // Enums for role and expertise
    public enum Role {
        ADMIN, FARMER, RECEPTIONIST, SPRAYER
    }

    public enum Expertise {
        APPRENTICE, ADEPT, EXPERT
    }

    // Ensure a default profile picture is set if not provided
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

    private String getDefaultAvatarUrlForRole(Role role) {
        // Handle invalid role
        return switch (role) {
            case ADMIN -> "admin-avatar-url";
            case FARMER -> "farmer-avatar-url";
            case RECEPTIONIST -> "receptionist-avatar-url";
            case SPRAYER -> "sprayer-avatar-url";
        };
    }
}
