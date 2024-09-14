package hoversprite.project.feedback;

import hoversprite.project.request.FeedbackRequest;
import hoversprite.project.response.FeedBackReponse;

import java.util.List;

public interface FeedbackGlobalService {
    FeedBackReponse createFeedback(Long orderId, FeedbackRequest feedbackRequest);

    List<FeedBackReponse> getFeedbacksBySprayOrderId(Long orderId);
//    FeedbackDTO getFeedback(Long id);
//    List<FeedBackReponse> getAllFeedbacks();
//    FeedBackReponse updateFeedback(Long id, FeedbackDTO feedbackDTO);
//    void deleteFeedback(Long id);
//    List<String> getExistingImageUrls(Long sprayOrderId);
//    void addImageUrlToFeedback(Long sprayOrderId, String imageUrl);
}
