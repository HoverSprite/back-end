package hoversprite.project.project.mapper;

import hoversprite.project.project.model.dto.PersonDTO;
import hoversprite.project.project.model.entity.Person;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PersonMapper {
    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    Person toEntity(PersonDTO dto);
    PersonDTO toDto(Person entity);
}
