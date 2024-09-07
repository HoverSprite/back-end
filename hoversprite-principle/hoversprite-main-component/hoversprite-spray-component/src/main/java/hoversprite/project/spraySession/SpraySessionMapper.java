package hoversprite.project.spraySession;

import hoversprite.project.request.SpraySessionRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
interface SpraySessionMapper {
    SpraySessionMapper INSTANCE = Mappers.getMapper(SpraySessionMapper.class);

    @Mapping(target = "id", ignore = true)
    SpraySession_2 toEntitySave(SpraySessionRequest request);

    SpraySession_2 toEntityUpdate(SpraySessionRequest request);
    SpraySessionDTO toDto(SpraySession_2 entity);
}
