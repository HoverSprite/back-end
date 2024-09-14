package hoversprite.project.sprayOrder;

import hoversprite.project.common.domain.CropType;
import hoversprite.project.common.domain.SprayStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SprayOrderDTO {
    private Long id;
    private Long farmer;
    private Long receptionist;
    private CropType cropType;
    private BigDecimal area;
    private LocalDateTime dateTime;
    private Double cost;
    private SprayStatus status;
    private BigDecimal paymentReceivedAmount;
    private BigDecimal changeAmount;
    private String location;
    private Double latitude;
    private Double longitude;
    private Long spraySession;
    private Boolean autoAssign;
}