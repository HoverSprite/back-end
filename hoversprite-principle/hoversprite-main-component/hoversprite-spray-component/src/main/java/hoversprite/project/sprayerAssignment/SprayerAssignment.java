package hoversprite.project.sprayerAssignment;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "SPRAYER_ASSIGNMENT")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
class SprayerAssignment {
    @Id
    @Column(name = "SPRAYER_ASSIGNMENT_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "SPRAY_ORDER_ID", nullable = false)
    private Long sprayOrder;

    @Column(name = "SPRAYER_ID", nullable = false)
    private Long sprayer;

    @Column(name = "IS_PRIMARY", nullable = false)
    private Boolean isPrimary;
}
