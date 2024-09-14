package hoversprite.project.auth.onetimecode;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
interface OneTimeCodeMapper {
    OneTimeCodeMapper INSTANCE = Mappers.getMapper(OneTimeCodeMapper.class);

    OneTimeCode toEntity(OneTimeCodeDTO dto);
    OneTimeCodeDTO toDto(OneTimeCode entity);
}
