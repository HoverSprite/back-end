package hoversprite.project.project.model.dto;

import hoversprite.project.project.model.entity.CropType;
import hoversprite.project.project.model.entity.SprayStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SprayOrderDTO {
    private Integer id;
    private PersonDTO farmer;
    private PersonDTO receptionist;
    private CropType cropType;
    private BigDecimal area;
    private LocalDateTime dateTime;
    private Double cost;
    private SprayStatus status;
    private BigDecimal paymentReceivedAmount;
    private BigDecimal changeAmount;
    private String location;
    private List<SprayerAssignmentDTO> sprayerAssignments;
    private SpraySessionDTO spraySession;
}
