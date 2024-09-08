package hoversprite.project.request;

import hoversprite.project.common.domain.CropType;
import hoversprite.project.common.domain.SprayStatus;
import hoversprite.project.payment.request.PaymentRequest;
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
public class SprayOrderRequest {
    private Long id;
    private PersonRequest farmer;
    private PersonRequest receptionist;
    private CropType cropType;
    private BigDecimal area;
    private LocalDateTime dateTime;
    private Double cost;
    private SprayStatus status;
    private BigDecimal paymentReceivedAmount;
    private BigDecimal changeAmount;
    private String location;
    private List<SprayerAssignmentRequest> sprayerAssignments;
    private SpraySessionRequest spraySession;
    private PaymentRequest payment;
}
