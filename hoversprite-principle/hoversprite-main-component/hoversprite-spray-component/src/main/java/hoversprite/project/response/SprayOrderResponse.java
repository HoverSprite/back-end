package hoversprite.project.response;

import hoversprite.project.common.domain.CropType;
import hoversprite.project.common.domain.SprayStatus;
import hoversprite.project.payment.request.PaymentResponse;
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
public class SprayOrderResponse {
    private Long id;
    private PersonResponse farmer;
    private PersonResponse receptionist;
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
    private List<SprayerAssignmentResponse> sprayerAssignments;
    private SpraySessionResponse spraySession;
    private PaymentResponse payment;
    private Boolean autoAssign;
}
