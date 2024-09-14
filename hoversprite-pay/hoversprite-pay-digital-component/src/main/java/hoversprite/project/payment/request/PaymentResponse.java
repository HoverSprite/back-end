package hoversprite.project.payment.request;

import hoversprite.project.common.domain.PayMethod;
import hoversprite.project.common.domain.PayStatus;
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
public class PaymentResponse {
    private Long id;
    private Long sprayOrder;
    private Long farmer;
    private BigDecimal amount;
    private PayMethod paymentMethod;
    private String transaction;
    private LocalDateTime paymentDate;
    private PayStatus status;
}
