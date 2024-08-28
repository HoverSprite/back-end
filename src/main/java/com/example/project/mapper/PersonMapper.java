package com.example.project.mapper;

import com.example.project.model.dto.PersonDTO;
import com.example.project.model.entity.Person;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PersonMapper {
    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    Person toEntity(PersonDTO dto);
    PersonDTO toDto(Person entity);
}
