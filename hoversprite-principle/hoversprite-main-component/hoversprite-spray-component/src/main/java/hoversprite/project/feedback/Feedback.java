package hoversprite.project.feedback;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "FEEDBACK")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FEEDBACK_ID")
    private long id;

    @Column(name = "SPRAY_ORDER_ID", nullable = false)
    private long sprayOrder;

    @Column(name = "COMMENT", nullable = false)
    private String comment;

    @Column(name = "RATE_OVERALL", nullable = false)
    private int overallRating;

    @Column(name = "RATE_ATTENTIVE", nullable = false)
    private int attentiveRating;

    @Column(name = "RATE_FRIENDLY", nullable = false)
    private int friendlyRating;

    @Column(name = "RATE_PROFESSIONAL", nullable = false)
    private int professionalRating;

    @Column(name = "DATE_TIME", nullable = false)
    private LocalDateTime submissionTime;
}