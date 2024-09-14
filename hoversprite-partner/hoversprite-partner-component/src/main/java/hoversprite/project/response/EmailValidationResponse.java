package hoversprite.project.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailValidationResponse {
    private boolean available;
    private String message;

    public EmailValidationResponse(boolean available) {
        this.available = available;
        this.message = available ? "Email is available" : "Email is already in use";
    }
}