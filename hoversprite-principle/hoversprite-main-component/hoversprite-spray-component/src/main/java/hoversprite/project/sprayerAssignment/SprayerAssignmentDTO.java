package hoversprite.project.sprayerAssignment;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SprayerAssignmentDTO {
    private Integer id;
    private Long sprayOrder;
    private Long sprayer;
    private Boolean isPrimary;
}