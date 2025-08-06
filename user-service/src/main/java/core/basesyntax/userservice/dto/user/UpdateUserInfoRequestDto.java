package core.basesyntax.userservice.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserInfoRequestDto(@NotBlank String fullName) {
}
