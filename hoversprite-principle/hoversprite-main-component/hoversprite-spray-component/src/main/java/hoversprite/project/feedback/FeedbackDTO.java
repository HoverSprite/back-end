package hoversprite.project.feedback;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackDTO {
    private Long id;
    private String comment;
    private int overallRating;
    private int attentiveRating;
    private int friendlyRating;
    private int professionalRating;
    private Long sprayOrderId;
    private List<String> imageUrls;
}