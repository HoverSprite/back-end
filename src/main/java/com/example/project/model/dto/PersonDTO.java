package com.example.project.model.dto;

import com.example.project.model.entity.PersonRole;
import lombok.*;

import java.util.List;
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