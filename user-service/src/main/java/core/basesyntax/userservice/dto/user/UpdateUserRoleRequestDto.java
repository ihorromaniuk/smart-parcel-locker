package core.basesyntax.userservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserRoleRequestDto(@Email(message = "is invalid")
                                       @NotBlank String email,
                                       @NotBlank String role) {
}
