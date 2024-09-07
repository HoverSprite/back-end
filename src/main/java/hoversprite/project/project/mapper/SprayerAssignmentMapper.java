package hoversprite.project.project.mapper;

import hoversprite.project.project.model.dto.SprayerAssignmentDTO;
import hoversprite.project.project.model.entity.SprayerAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SprayerAssignmentMapper {
    SprayerAssignmentMapper INSTANCE = Mappers.getMapper(SprayerAssignmentMapper.class);

    SprayerAssignment toEntity(SprayerAssignmentDTO dto);
    SprayerAssignmentDTO toDto(SprayerAssignment entity);
}
