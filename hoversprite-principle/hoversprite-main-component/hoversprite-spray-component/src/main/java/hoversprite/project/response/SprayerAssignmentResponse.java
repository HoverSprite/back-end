package hoversprite.project.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SprayerAssignmentResponse {
    private Long id;
    private PersonResponse sprayer;
    private Long sprayOrder;
    private Boolean isPrimary;
}