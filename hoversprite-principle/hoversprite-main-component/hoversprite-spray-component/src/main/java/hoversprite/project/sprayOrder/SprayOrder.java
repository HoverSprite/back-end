package hoversprite.project.sprayOrder;

import hoversprite.project.common.domain.CropType;
import hoversprite.project.common.domain.SprayStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "SPRAY_ORDER")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
class SprayOrder {
    @Id
    @Column(name = "SPRAY_ORDER_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "FARMER_ID")
    private Long farmer;

    @Column(name = "RECEPTIONIST_ID")
    private Long receptionist;

    @Column(name = "CROP_TYPE", nullable = false)
    private CropType cropType;

    @Column(name = "AREA", precision = 10, scale = 2, nullable = false)
    private BigDecimal area;

    @Column(name = "DATE_TIME", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "COST", precision = 12)
    private Double cost;

    @Column(name = "STATUS")
    private SprayStatus status;

    @Column(name = "PAYMENT_RECEIVED_AMOUNT", precision = 12, scale = 2)
    private BigDecimal paymentReceivedAmount;

    @Column(name = "CHANGE_AMOUNT", precision = 12, scale = 2)
    private BigDecimal changeAmount;

    @Column(name = "LOCATION", nullable = false)
    private String location;

    @Column(name = "LATITUDE")
    private Double latitude;

    @Column(name = "LONGITUDE")
    private Double longitude;

    @Column(name = "SPRAY_SESSION_ID")
    private Long spraySession;
}