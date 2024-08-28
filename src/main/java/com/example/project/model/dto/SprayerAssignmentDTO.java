package com.example.project.model.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SprayerAssignmentDTO {
    private Integer id;
    private SprayOrderDTO sprayOrder;
    private PersonDTO sprayer;
    private Boolean isPrimary;
}