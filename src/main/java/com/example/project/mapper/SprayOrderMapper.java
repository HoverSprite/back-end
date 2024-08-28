package com.example.project.mapper;

import com.example.project.model.dto.SprayOrderDTO;
import com.example.project.model.entity.SprayOrder;
import com.example.project.model.entity.SprayStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper( unmappedTargetPolicy = ReportingPolicy.ERROR, uses = { PersonMapper.class, SprayerAssignmentMapper.class, SpraySessionMapper.class })
public interface SprayOrderMapper {
    SprayOrderMapper INSTANCE = Mappers.getMapper(SprayOrderMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "receptionist", ignore = true)
    @Mapping(target = "spraySession", ignore = true)
    @Mapping(target = "sprayerAssignments", ignore = true)
    @Mapping(target = "status", expression = "java(mapStatusToPending())")
    SprayOrder toEntitySave(SprayOrderDTO dto);
    SprayOrderDTO toDto(SprayOrder entity);

    default SprayStatus mapStatusToPending() {
        return SprayStatus.PENDING;
    }
}
