package hoversprite.project.feedback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping()
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private  FileUploadService fileUploadService;

    @PostMapping("/addFeedback/{sprayOrderId}")
    public ResponseEntity<FeedbackDTO> createFeedback(@PathVariable Long sprayOrderId, @RequestBody FeedbackDTO feedbackDTO) {
        feedbackDTO.setSprayOrderId(sprayOrderId);
        FeedbackDTO savedFeedback = feedbackService.createFeedback(feedbackDTO);
        return ResponseEntity.ok(savedFeedback);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackDTO> getFeedback(@PathVariable Long id) {
        FeedbackDTO feedback = feedbackService.getFeedback(id);
        return ResponseEntity.ok(feedback);
    }

    @GetMapping
    public ResponseEntity<List<FeedbackDTO>> getAllFeedbacks() {
        List<FeedbackDTO> feedbacks = feedbackService.getAllFeedbacks();
        return ResponseEntity.ok(feedbacks);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeedbackDTO> updateFeedback(@PathVariable Long id, @RequestBody FeedbackDTO feedbackDTO) {
        FeedbackDTO updatedFeedback = feedbackService.updateFeedback(id, feedbackDTO);
        return ResponseEntity.ok(updatedFeedback);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/upload-image/{sprayOrderId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(
            @PathVariable Long sprayOrderId,
            @RequestParam("image") MultipartFile image) throws IOException {

        List<String> existingImages = feedbackService.getExistingImageUrls(sprayOrderId);
        if (existingImages.size() >= 5) {
            return ResponseEntity.badRequest().body("Maximum number of images (5) already uploaded for this order.");
        }

        String imageUrl = fileUploadService.uploadFile(image);
        feedbackService.addImageUrlToFeedback(sprayOrderId, imageUrl);

        return ResponseEntity.ok(imageUrl);
    }
}