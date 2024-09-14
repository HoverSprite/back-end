package hoversprite.project.image;

import hoversprite.project.request.ImageRequest;

import java.util.List;
public interface ImageGlobalService {
    List<ImageDTO> createImages(List<ImageRequest> request);

    List<ImageDTO> findImagesByFeedbackIds(List<Long> feedbackIds);
}
