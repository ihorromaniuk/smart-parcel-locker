package core.basesyntax.userservice.event;

import java.io.Serializable;

public record UserDisabledEvent(Long id,
                                String email,
                                String fullName) implements Serializable {
}
