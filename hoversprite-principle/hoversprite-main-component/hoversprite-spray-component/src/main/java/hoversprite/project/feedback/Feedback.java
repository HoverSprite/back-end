package hoversprite.project.feedback;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "ORDER_FEEDBACK")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "spray_order_id", nullable = false)
    private Long sprayOrderId;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private int overallRating;

    @Column(nullable = false)
    private int attentiveRating;

    @Column(nullable = false)
    private int friendlyRating;

    @Column(nullable = false)
    private int professionalRating;

    @Column(nullable = false)
    private LocalDateTime submissionTime;

    @ElementCollection
    @CollectionTable(name = "feedback_images", joinColumns = @JoinColumn(name = "feedback_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;
}