package hoversprite.project.mapper;

import hoversprite.project.response.SprayOrderResponse;
import hoversprite.project.sprayOrder.SprayOrderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SprayOrderResponseMapper {
    SprayOrderResponseMapper INSTANCE = Mappers.getMapper(SprayOrderResponseMapper.class);

//    SprayOrderResponse toResponse(SprayOrderDTO request);
}