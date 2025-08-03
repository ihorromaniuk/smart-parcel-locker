package core.basesyntax.userservice.dto.exception;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public record ExceptionDto(HttpStatus status,
                           LocalDateTime timestamp,
                           String message) {
    public ExceptionDto(HttpStatus status, String message) {
        this(status, LocalDateTime.now(), message);
    }
}
