package hoversprite.project.feedback;

import java.util.List;

interface FeedbackRepositoryCustom {
    List<Feedback> getFeedbacksBySprayOrderId(Long sprayOrderId);
}
