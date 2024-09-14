package hoversprite.project.response;

import hoversprite.project.request.ImageRequest;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedBackReponse {
    private Long id;
    private String comment;
    private Integer overallRating;
    private Integer attentiveRating;
    private Integer friendlyRating;
    private Integer professionalRating;
    private Long sprayOrder;
    private List<ImageResponse> images;
}
