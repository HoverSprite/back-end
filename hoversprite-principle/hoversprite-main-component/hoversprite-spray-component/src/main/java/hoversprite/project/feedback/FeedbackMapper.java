package hoversprite.project.feedback;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FeedbackMapper {
    FeedbackMapper INSTANCE = Mappers.getMapper(FeedbackMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "submissionTime", ignore = true)
    Feedback toEntity(FeedbackDTO dto);

    FeedbackDTO toDto(Feedback entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "submissionTime", expression = "java(java.time.LocalDateTime.now())")
    Feedback toEntitySave(FeedbackDTO dto);
}