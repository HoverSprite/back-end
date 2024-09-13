package hoversprite.project.image;

import java.util.List;

interface ImageRepositoryCustom {
    List<Image> getImagesByFeedbackIds(List<Long> feedbackIds);
}
