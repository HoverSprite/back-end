package hoversprite.project.mapper;

import hoversprite.project.response.SprayerAssignmentResponse;
import hoversprite.project.sprayerAssignment.SprayerAssignmentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SprayerAssignmentResponseMapper {
    SprayerAssignmentResponseMapper INSTANCE = Mappers.getMapper(SprayerAssignmentResponseMapper.class);

//    SprayerAssignmentResponse toResponse(SprayerAssignmentDTO dto);
}
