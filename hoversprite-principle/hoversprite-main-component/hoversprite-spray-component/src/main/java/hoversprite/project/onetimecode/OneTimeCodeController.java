package hoversprite.project.onetimecode;


import hoversprite.project.common.domain.PersonRole;
import hoversprite.project.onetimecode.request.OTCRequest;
import hoversprite.project.qrcode.QRCodeGlobalService;
import hoversprite.project.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.zxing.WriterException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@CrossOrigin
@RequestMapping("/otp")
class OneTimeCodeController {

    @Autowired
    private OneTimeCodeService oneTimeCodeService;

    @Autowired
    private QRCodeGlobalService qrCodeGlobalService;
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
    @PreAuthorize("hasAnyRole('FARMER')")
    public ResponseEntity<String> verifyOtp(@PathVariable Long orderId) {
        Long userId = SecurityUtils.getCurrentUserId();

        if (oneTimeCodeService.verifyOtp(userId, orderId)) {
            return ResponseEntity.ok("OTP verified successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid One Time Code");
        }
    }

    @GetMapping("/qr/{orderId}")
    @PreAuthorize("hasAnyRole('SPRAYER')")
    public ResponseEntity<byte[]> generateQRCode(@PathVariable Long orderId, @RequestParam String content) {
        try {
            byte[] qrCodeImage = qrCodeGlobalService.generateQRCode(content);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(qrCodeImage.length);

            return new ResponseEntity<>(qrCodeImage, headers, HttpStatus.OK);
        } catch (WriterException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
