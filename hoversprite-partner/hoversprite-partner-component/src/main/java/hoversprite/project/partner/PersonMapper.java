package hoversprite.project.partner;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
interface PersonMapper {
    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    Person toEntity(PersonDTO dto);
    PersonDTO toDto(Person entity);
}
