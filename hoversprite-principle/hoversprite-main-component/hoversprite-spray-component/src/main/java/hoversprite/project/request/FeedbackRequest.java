package hoversprite.project.request;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackRequest {
    private Long id;
    private String comment;
    private Integer overallRating;
    private Integer attentiveRating;
    private Integer friendlyRating;
    private Integer professionalRating;
    private Long sprayOrder;
    private List<ImageRequest> images;
}
