package hoversprite.project.image;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "IMAGE")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
class Image {

    @Id
    @Column(name = "IMAGE_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob // Large object
    @Column(name = "IMAGE_STR", nullable = false)
    private String imageStr;

    @Column(name = "FEEDBACK_ID", nullable = false)
    private long feedback;
}