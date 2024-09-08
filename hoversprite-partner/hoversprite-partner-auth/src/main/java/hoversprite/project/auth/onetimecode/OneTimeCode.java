package hoversprite.project.auth.onetimecode;

import hoversprite.project.common.domain.PersonExpertise;
import hoversprite.project.common.domain.PersonRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "OTC")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
class OneTimeCode {

    @Id
    @Column(name = "OTC_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USER_ID", nullable = false)
    private Long user;

    @Column(name = "OTC_CODE", nullable = false)
    private String otpCode;

    @Column(name = "DATE_TIME", nullable = false)
    private LocalDateTime createdAt;
}
