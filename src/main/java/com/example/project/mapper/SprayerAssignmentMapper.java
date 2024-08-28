package com.example.project.mapper;

import com.example.project.model.dto.SprayerAssignmentDTO;
import com.example.project.model.entity.SprayerAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SprayerAssignmentMapper {
    SprayerAssignmentMapper INSTANCE = Mappers.getMapper(SprayerAssignmentMapper.class);

    SprayerAssignment toEntity(SprayerAssignmentDTO dto);
    SprayerAssignmentDTO toDto(SprayerAssignment entity);
}
