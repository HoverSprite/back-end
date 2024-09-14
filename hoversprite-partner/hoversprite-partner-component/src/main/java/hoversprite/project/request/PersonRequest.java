package hoversprite.project.request;

import hoversprite.project.common.domain.PersonExpertise;
import hoversprite.project.common.domain.PersonRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonRequest {
        private String fullName;
        private String phoneNumber;
        private String homeAddress;
        private String emailAddress;
        private String passwordHash;
        private PersonRole role;
        private PersonExpertise expertise;
}
