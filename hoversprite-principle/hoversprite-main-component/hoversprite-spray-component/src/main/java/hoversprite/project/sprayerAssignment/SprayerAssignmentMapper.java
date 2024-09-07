package hoversprite.project.sprayerAssignment;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
interface SprayerAssignmentMapper {
    SprayerAssignmentMapper INSTANCE = Mappers.getMapper(SprayerAssignmentMapper.class);

    SprayerAssignment toEntity(SprayerAssignmentDTO dto);
    SprayerAssignmentDTO toDto(SprayerAssignment entity);
}
