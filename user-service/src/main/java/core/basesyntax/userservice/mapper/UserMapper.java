package core.basesyntax.userservice.mapper;

import core.basesyntax.userservice.config.MapperConfig;
import core.basesyntax.userservice.dto.user.CreateUserRequestDto;
import core.basesyntax.userservice.dto.user.UserResponseDto;
import core.basesyntax.userservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User toModel(CreateUserRequestDto requestDto);

    UserResponseDto toDto(User user);
}
