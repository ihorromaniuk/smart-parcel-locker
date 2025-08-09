package core.basesyntax.userservice.event;

import java.io.Serializable;

public record UserCreatedEvent(Long id,
                               String email,
                               String fullName) implements Serializable {
}
