package hoversprite.project.image;

import lombok.*;

@Setter
@Getter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO {
    private Long id;
    private String imageStr;
    private Long feedback;
}
