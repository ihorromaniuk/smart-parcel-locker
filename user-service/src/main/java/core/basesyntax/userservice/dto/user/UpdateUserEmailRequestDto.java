package core.basesyntax.userservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserEmailRequestDto(@Email(message = "is invalid")
                                        @NotBlank String email) {
}
