package hoversprite.project.spraySession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/spray-sessions")
//@CrossOrigin(allowedOriginPatterns = "*", allowCredentials = "true")
public class SpraySessionController {

    @Autowired
    private SpraySession2Service spraySessionService;

    @GetMapping("/available-slots")
    public ResponseEntity<Map<LocalDate, List<LocalTime>>> getAvailableSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        Map<LocalDate, List<LocalTime>> availableSlots = spraySessionService.getAvailableSlotsForWeek(startDate);
        return ResponseEntity.ok(availableSlots);
    }
}