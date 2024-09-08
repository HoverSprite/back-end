package hoversprite.project.payment;

import hoversprite.project.common.domain.PayMethod;
import hoversprite.project.common.domain.PayStatus;
import jakarta.persistence.Column;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private Long id;
    private Long sprayOrder;
    private Long farmer;
    private BigDecimal amount;
    private PayMethod paymentMethod;
    private String transaction;
    private LocalDateTime paymentDate;
    private PayStatus status;
}