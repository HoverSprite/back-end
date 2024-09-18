package hoversprite.project.feedback;

import hoversprite.project.request.FeedbackRequest;
import hoversprite.project.response.FeedBackReponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping
class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("/orders/{orderId}/feedbacks/")
    @PreAuthorize("hasAnyRole('FARMER')")
    public ResponseEntity<FeedBackReponse> createFeedback(@PathVariable Long orderId, @RequestBody FeedbackRequest feedbackRequest) {
        FeedBackReponse savedFeedback = feedbackService.createFeedback(orderId, feedbackRequest);
        return ResponseEntity.ok(savedFeedback);
    }
}