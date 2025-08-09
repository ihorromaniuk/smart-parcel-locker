package core.basesyntax.userservice.event;

import java.io.Serializable;

public record UserEnabledEvent(Long id,
                               String email,
                               String fullName) implements Serializable {
}
