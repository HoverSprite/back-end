package hoversprite.project.mapper;

import hoversprite.project.response.SpraySessionResponse;
import hoversprite.project.spraySession.SpraySessionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SpraySessionResponseMapper {
    SpraySessionResponseMapper INSTANCE = Mappers.getMapper(SpraySessionResponseMapper.class);

    SpraySessionResponse toResponse(SpraySessionDTO request);
}
