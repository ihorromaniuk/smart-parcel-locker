package core.basesyntax.userservice.dto.user;

import core.basesyntax.userservice.model.Role;
import java.util.Set;

public record UserResponseDto(Long id,
                              String email,
                              String fullName,
                              Set<Role> roles) {
}
