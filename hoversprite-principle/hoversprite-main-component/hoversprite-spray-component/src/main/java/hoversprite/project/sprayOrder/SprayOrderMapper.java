package hoversprite.project.sprayOrder;

import hoversprite.project.common.domain.SprayStatus;
import hoversprite.project.request.SprayOrderRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
interface SprayOrderMapper {
    SprayOrderMapper INSTANCE = Mappers.getMapper(SprayOrderMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "receptionist", ignore = true)
    @Mapping(target = "spraySession", ignore = true)
    @Mapping(target = "status", expression = "java(mapStatusToPending())")
    @Mapping(target = "farmer", ignore = true) // Ignore the farmer field to prevent recursion
    SprayOrder toEntitySave(SprayOrderRequest request);


    SprayOrderDTO toDto(SprayOrder entity);

    default SprayStatus mapStatusToPending() {
        return SprayStatus.PENDING;
    }
}

