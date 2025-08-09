package core.basesyntax.userservice.mapper;

import core.basesyntax.userservice.config.MapperConfig;
import core.basesyntax.userservice.dto.user.CreateUserRequestDto;
import core.basesyntax.userservice.dto.user.UserResponseDto;
import core.basesyntax.userservice.event.UserCreatedEvent;
import core.basesyntax.userservice.event.UserDisabledEvent;
import core.basesyntax.userservice.event.UserEnabledEvent;
import core.basesyntax.userservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User toModel(CreateUserRequestDto requestDto);

    UserResponseDto toDto(User user);

    UserCreatedEvent modelToCreatedEvent(User user);

    UserDisabledEvent modelToDisabledEvent(User user);

    UserEnabledEvent modelToEnabledEvent(User user);
}
