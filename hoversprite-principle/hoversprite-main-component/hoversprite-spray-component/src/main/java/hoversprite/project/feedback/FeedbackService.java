package hoversprite.project.feedback;

import java.util.List;

public interface FeedbackService {
    FeedbackDTO createFeedback(FeedbackDTO feedbackDTO);
    FeedbackDTO getFeedback(Long id);
    List<FeedbackDTO> getAllFeedbacks();
    FeedbackDTO updateFeedback(Long id, FeedbackDTO feedbackDTO);
    void deleteFeedback(Long id);
    List<String> getExistingImageUrls(Long sprayOrderId);
    void addImageUrlToFeedback(Long sprayOrderId, String imageUrl);
}