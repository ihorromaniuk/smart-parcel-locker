package core.basesyntax.userservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DisableUserRequestDto(@Email @NotBlank String email) {
}
