package hoversprite.project.mapper;

import hoversprite.project.feedback.FeedbackDTO;
import hoversprite.project.image.ImageDTO;
import hoversprite.project.response.FeedBackReponse;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE, uses = ImageResponseMapper.class)
public interface FeedbackResponseMapper {
    FeedbackResponseMapper INSTANCE = Mappers.getMapper(FeedbackResponseMapper.class);

    @Mapping(source = "feedbackDTO.id", target = "id")
    @Mapping(source = "feedbackDTO.comment", target = "comment")
    @Mapping(source = "feedbackDTO.overallRating", target = "overallRating")
    @Mapping(source = "feedbackDTO.attentiveRating", target = "attentiveRating")
    @Mapping(source = "feedbackDTO.friendlyRating", target = "friendlyRating")
    @Mapping(source = "feedbackDTO.professionalRating", target = "professionalRating")
    @Mapping(source = "feedbackDTO.sprayOrder", target = "sprayOrder")
    @Mapping(source = "imageDTOs", target = "images")
    FeedBackReponse toResponse(FeedbackDTO feedbackDTO, List<ImageDTO> imageDTOs);
}
