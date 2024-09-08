package hoversprite.project.project.model.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SprayerAssignmentDTO {
    private Long id;
    private PersonDTO sprayer;
    private Boolean isPrimary;
}