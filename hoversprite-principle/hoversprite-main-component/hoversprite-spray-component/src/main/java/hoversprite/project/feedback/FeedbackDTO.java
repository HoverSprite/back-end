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
    private Integer overallRating;
    private Integer attentiveRating;
    private Integer friendlyRating;
    private Integer professionalRating;
    private Long sprayOrder;
}