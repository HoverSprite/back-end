package hoversprite.project.auth.onetimecode;


import hoversprite.project.auth.onetimecode.request.OTCRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/user/{userId}/otp")
class OneTimeCodeController {

    @Autowired
    private OneTimeCodeService oneTimeCodeService;

    @PostMapping("/create")
    public ResponseEntity<String> createOtp(@PathVariable Long userId) {
        try {
            String code = oneTimeCodeService.createOtp(userId);
            return ResponseEntity.ok(code);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/verify/{orderId}")
    public ResponseEntity<String> verifyOtp(@PathVariable Long userId, @PathVariable Long orderId, @RequestBody OTCRequest otp) {
        if (oneTimeCodeService.verifyOtp(orderId, otp)) {
            return ResponseEntity.ok("OTP verified successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid One Time Code");
        }
    }
}
