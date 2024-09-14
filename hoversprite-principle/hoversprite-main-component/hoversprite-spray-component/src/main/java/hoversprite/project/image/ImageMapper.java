package hoversprite.project.image;

import hoversprite.project.mapper.SprayerAssignmentResponseMapper;
import hoversprite.project.request.ImageRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
interface ImageMapper {
    ImageMapper INSTANCE = Mappers.getMapper(ImageMapper.class);

    @Mapping(target = "id", ignore = true)
    Image toEntitySave(ImageRequest request);

    ImageDTO toDto(Image image);
}
