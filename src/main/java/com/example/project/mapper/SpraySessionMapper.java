package com.example.project.mapper;

import com.example.project.model.dto.SpraySessionDTO;
import com.example.project.model.entity.SpraySession_2;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SpraySessionMapper {
    SpraySessionMapper INSTANCE = Mappers.getMapper(SpraySessionMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sprayOrder", ignore = true)
    SpraySession_2 toEntitySave(SpraySessionDTO dto);
    SpraySessionDTO toDto(SpraySession_2 entity);
}
