package hoversprite.project.request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SprayerAssignmentRequest {
    private Long id;
    private PersonRequest sprayer;
    private Long sprayOrder;
    private Boolean isPrimary;
}