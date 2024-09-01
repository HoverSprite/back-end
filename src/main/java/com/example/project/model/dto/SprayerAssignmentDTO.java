package com.example.project.model.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SprayerAssignmentDTO {
    private Integer id;
    private PersonDTO sprayer;
    private Boolean isPrimary;
}