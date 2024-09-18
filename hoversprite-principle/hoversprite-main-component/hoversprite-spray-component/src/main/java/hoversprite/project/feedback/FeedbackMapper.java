package hoversprite.project.feedback;

import hoversprite.project.request.FeedbackRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
interface FeedbackMapper {
    FeedbackMapper INSTANCE = Mappers.getMapper(FeedbackMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "submissionTime", ignore = true)
    Feedback toEntity(FeedbackDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "submissionTime", ignore = true)
    Feedback toEntitySave(FeedbackRequest request);

    FeedbackDTO toDto(Feedback entity);
}