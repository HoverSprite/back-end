package hoversprite.project.request;

import lombok.*;

@Setter
@Getter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageRequest {
    private Long id;
    private String imageStr;
    private Long feedback;
}
