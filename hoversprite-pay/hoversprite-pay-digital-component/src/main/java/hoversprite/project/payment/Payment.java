package hoversprite.project.payment;

import hoversprite.project.common.domain.PayMethod;
import hoversprite.project.common.domain.PayStatus;
import hoversprite.project.common.domain.PersonExpertise;
import hoversprite.project.common.domain.PersonRole;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "PAYMENT")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
class Payment {
    @Id
    @Column(name = "PAYMENT_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "SPRAY_ORDER_ID", nullable = false)
    private Long sprayOrder;

    @Column(name = "FARMER_ID", nullable = false)
    private Long farmer;

    @Column(name = "AMOUNT", precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "PAYMENT_METHOD", nullable = false)
    private PayMethod paymentMethod;

    @Column(name = "TRANSACTION_ID")
    private String transaction;

    @Column(name = "PAYMENT_DATE", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "STATUS", nullable = false)
    private PayStatus status;
}
