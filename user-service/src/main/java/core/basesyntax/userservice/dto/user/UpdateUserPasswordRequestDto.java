package core.basesyntax.userservice.dto.user;

import core.basesyntax.userservice.annotation.fieldmatch.FieldsMatch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@FieldsMatch(field = "password",
        fieldMatch = "repeatPassword",
        message = "Passwords don't match")
public record UpdateUserPasswordRequestDto(@NotBlank
                                           @Size(min = 8, max = 24)
                                           String password,
                                           String repeatPassword) {
}
