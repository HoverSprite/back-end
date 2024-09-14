package hoversprite.project.mapper;

import hoversprite.project.partner.PersonDTO;
import hoversprite.project.response.PersonResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PersonResponseMapper {
    PersonResponseMapper INSTANCE = Mappers.getMapper(PersonResponseMapper.class);

    PersonResponse toReponse(PersonDTO dto);
}
