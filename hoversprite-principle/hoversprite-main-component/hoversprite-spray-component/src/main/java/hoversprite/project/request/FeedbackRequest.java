package hoversprite.project.request;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class FeedbackRequest {
    private Long sprayOrderId;
    private int rating;
    private String comment;
    private LocalDateTime submissionTime;
}
