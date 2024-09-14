package hoversprite.project.mapper;

import hoversprite.project.image.ImageDTO;
import hoversprite.project.response.ImageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ImageResponseMapper {
    ImageResponseMapper INSTANCE = Mappers.getMapper(ImageResponseMapper.class);

    ImageResponse toResponse(ImageDTO dto);
}
