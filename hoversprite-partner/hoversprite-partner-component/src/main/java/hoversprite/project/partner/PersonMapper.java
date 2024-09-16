package hoversprite.project.partner;

import hoversprite.project.common.domain.PersonAuthor;
import hoversprite.project.request.PersonRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
interface PersonMapper {
    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    Person toEntity(PersonRequest request);
    PersonDTO toDto(Person entity);

    Person toEntitySave(PersonDTO personDTO);

    PersonAuthor toAuthor(PersonDTO personDTO);
}
