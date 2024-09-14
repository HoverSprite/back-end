package hoversprite.project.feedback;

import hoversprite.project.common.domain.SprayStatus;
import hoversprite.project.image.ImageDTO;
import hoversprite.project.image.ImageGlobalService;
import hoversprite.project.mapper.FeedbackResponseMapper;
import hoversprite.project.request.FeedbackRequest;
import hoversprite.project.request.ImageRequest;
import hoversprite.project.response.FeedBackReponse;
import hoversprite.project.sprayOrder.SprayOrderDTO;
import hoversprite.project.sprayOrder.SprayOrderGlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ImageGlobalService imageGlobalService;

//    @Override
//    public FeedbackDTO createFeedback(FeedbackDTO feedbackDTO) {
//        SprayOrderDTO sprayOrderDTO = sprayOrderGlobalService.findById(feedbackDTO.getSprayOrder());
//        if (sprayOrderDTO == null) {
//            throw new RuntimeException("Spray order not found with id: " + feedbackDTO.getSprayOrder());
//        }
//
//        if (sprayOrderDTO.getStatus() != SprayStatus.COMPLETED) {
//            throw new RuntimeException("Feedback can only be given for completed orders.");
//        }
//
////        if (feedbackDTO.getImageUrls() != null && feedbackDTO.getImageUrls().size() > 5) {
////            throw new IllegalArgumentException("A maximum of 5 images can be uploaded.");
////        }
//
//        Feedback feedback = feedbackMapper.toEntity(feedbackDTO);
//        feedback.setSubmissionTime(LocalDateTime.now());
//
//        Feedback savedFeedback = feedbackRepository.save(feedback);
//        return feedbackMapper.toDto(savedFeedback);
//    }

    @Override
    public FeedBackReponse createFeedback(Long orderId, FeedbackRequest feedbackRequest) {
        feedbackRequest.setSprayOrder(orderId);
        Feedback feedback = FeedbackMapper.INSTANCE.toEntitySave(feedbackRequest);
        feedback.setSubmissionTime(LocalDateTime.now());
        Feedback savedFeedback = feedbackRepository.save(feedback);

        List<ImageDTO> savedImages = new ArrayList<>();

        if (feedbackRequest.getImages() != null) {
            if (feedbackRequest.getImages().size() > 5) {
                throw new IllegalArgumentException("A maximum of 5 images can be uploaded.");
            }
            feedbackRequest.getImages().forEach(images -> images.setFeedback(savedFeedback.getId()));
            savedImages = imageGlobalService.createImages(feedbackRequest.getImages());
        }
        FeedbackDTO feedbackDTO = FeedbackMapper.INSTANCE.toDto(savedFeedback);
        return FeedbackResponseMapper.INSTANCE.toResponse(feedbackDTO, savedImages);
    }

    @Override
    public List<FeedBackReponse> getFeedbacksBySprayOrderId(Long orderId) {
        List<Feedback> feedbacks = feedbackRepository.getFeedbacksBySprayOrderId(orderId);
        if (CollectionUtils.isEmpty(feedbacks)) {
            return Collections.emptyList();
        }
        List<Long> feedbackIds = feedbacks.stream().map(Feedback::getId).collect(Collectors.toList());
        Map<Long, List<ImageDTO>> imagesGroupedByFeedbackId = imageGlobalService.findImagesByFeedbackIds(feedbackIds).stream().collect(Collectors.groupingBy(ImageDTO::getFeedback));

        return feedbacks.stream().map(feedback -> {
            FeedbackDTO feedbackDTO = FeedbackMapper.INSTANCE.toDto(feedback);
            List<ImageDTO> imagesForFeedback = imagesGroupedByFeedbackId.getOrDefault(feedback.getId(), List.of());
            return FeedbackResponseMapper.INSTANCE.toResponse(feedbackDTO, imagesForFeedback);
        }).collect(Collectors.toList());
    }

//    @Override
//    public FeedbackDTO getFeedback(Long id) {
//        Feedback feedback = feedbackRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));
//        return feedbackMapper.toDto(feedback);
//    }
//
//    @Override
//    public List<FeedbackDTO> getAllFeedbacks() {
//        return feedbackRepository.findAll().stream()
//                .map(feedbackMapper::toDto)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public FeedbackDTO updateFeedback(Long id, FeedbackDTO feedbackDTO) {
//        Feedback existingFeedback = feedbackRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));
//
//        existingFeedback.setComment(feedbackDTO.getComment());
//        existingFeedback.setOverallRating(feedbackDTO.getOverallRating());
//        existingFeedback.setAttentiveRating(feedbackDTO.getAttentiveRating());
//        existingFeedback.setFriendlyRating(feedbackDTO.getFriendlyRating());
//        existingFeedback.setProfessionalRating(feedbackDTO.getProfessionalRating());
//
////        if (feedbackDTO.getImageUrls() != null) {
////            if (feedbackDTO.getImageUrls().size() > 5) {
////                throw new IllegalArgumentException("A maximum of 5 images can be uploaded.");
////            }
////            existingFeedback.setImageUrls(feedbackDTO.getImageUrls());
////        }
//
//        Feedback updatedFeedback = feedbackRepository.save(existingFeedback);
//        return feedbackMapper.toDto(updatedFeedback);
//    }
//
//    @Override
//    public void deleteFeedback(Long id) {
//        feedbackRepository.deleteById(id);
//    }
//
//    @Override
//    public List<String> getExistingImageUrls(Long sprayOrderId) {
//        return null;
////        return feedbackRepository.findBySprayOrderId(sprayOrderId)
////                .map(Feedback::getImageUrls)
////                .orElse(new ArrayList<>());
//    }
//
//    @Override
//    public void addImageUrlToFeedback(Long sprayOrderId, String imageUrl) {
////        Feedback feedback = feedbackRepository.findBySprayOrderId(sprayOrderId)
////                .orElseThrow(() -> new RuntimeException("Feedback not found for spray order id: " + sprayOrderId));
////
////        if (feedback.getImageUrls().size() >= 5) {
////            throw new IllegalStateException("Maximum number of images (5) already uploaded for this feedback.");
////        }
////
////        feedback.getImageUrls().add(imageUrl);
////        Feedback updatedFeedback = feedbackRepository.save(feedback);
////        feedbackMapper.toDto(updatedFeedback);
//    }
}