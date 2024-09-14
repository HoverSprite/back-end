package hoversprite.project.feedback;

import hoversprite.project.request.FeedbackRequest;
import hoversprite.project.response.FeedBackReponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/user/{userId}")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private  FileUploadService fileUploadService;

    @PostMapping("/orders/{orderId}/feedbacks/")
    public ResponseEntity<FeedBackReponse> createFeedback(@PathVariable Long orderId, @RequestBody FeedbackRequest feedbackRequest) {
        FeedBackReponse savedFeedback = feedbackService.createFeedback(orderId, feedbackRequest);
        return ResponseEntity.ok(savedFeedback);
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<FeedbackDTO> getFeedback(@PathVariable Long id) {
//        FeedbackDTO feedback = feedbackService.getFeedback(id);
//        return ResponseEntity.ok(feedback);
//    }
//
//    @GetMapping
//    public ResponseEntity<List<FeedbackDTO>> getAllFeedbacks() {
//        List<FeedbackDTO> feedbacks = feedbackService.getAllFeedbacks();
//        return ResponseEntity.ok(feedbacks);
//    }
//
//    @PutMapping("/orders/{orderId}/feedbacks/{id}")
//    public ResponseEntity<FeedbackDTO> updateFeedback(@PathVariable Long id, @RequestBody FeedbackDTO feedbackDTO) {
//        FeedbackDTO updatedFeedback = feedbackService.updateFeedback(id, feedbackDTO);
//        return ResponseEntity.ok(updatedFeedback);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
//        feedbackService.deleteFeedback(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PostMapping(value = "/orders/{orderId}/feedbacks/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<String> uploadImage(
//            @PathVariable Long sprayOrderId,
//            @RequestParam("image") MultipartFile image) throws IOException {
//
//        List<String> existingImages = feedbackService.getExistingImageUrls(sprayOrderId);
//        if (existingImages.size() >= 5) {
//            return ResponseEntity.badRequest().body("Maximum number of images (5) already uploaded for this order.");
//        }
//
//        String imageUrl = fileUploadService.uploadFile(image);
//        feedbackService.addImageUrlToFeedback(sprayOrderId, imageUrl);
//
//        return ResponseEntity.ok(imageUrl);
//    }
}