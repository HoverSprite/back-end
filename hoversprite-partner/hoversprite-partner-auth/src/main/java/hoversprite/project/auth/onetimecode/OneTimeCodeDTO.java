package hoversprite.project.auth.onetimecode;

import hoversprite.project.common.domain.PersonExpertise;
import hoversprite.project.common.domain.PersonRole;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OneTimeCodeDTO {
    private Long id;
    private Long user;
    private String otpCode;
    private LocalDateTime createdAt;
}