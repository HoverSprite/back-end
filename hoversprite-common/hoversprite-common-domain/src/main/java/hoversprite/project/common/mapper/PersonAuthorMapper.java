package hoversprite.project.common.mapper;


import hoversprite.project.common.domain.PersonAuthor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PersonAuthorMapper {
    PersonAuthorMapper INSTANCE = Mappers.getMapper(PersonAuthorMapper.class);

}
