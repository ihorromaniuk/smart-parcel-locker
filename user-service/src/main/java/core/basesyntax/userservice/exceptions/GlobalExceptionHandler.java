package core.basesyntax.userservice.exceptions;

import core.basesyntax.userservice.dto.exception.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({EntityNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<ExceptionDto> handleNotFoundException(
            RuntimeException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ExceptionDto exceptionDto = new ExceptionDto(status, ex.getMessage());
        return new ResponseEntity<>(exceptionDto, status);
    }

    @ExceptionHandler({RegistrationException.class})
    public ResponseEntity<ExceptionDto> handleRegistrationException(
            RuntimeException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        ExceptionDto exceptionDto = new ExceptionDto(status, ex.getMessage());
        return new ResponseEntity<>(exceptionDto, status);
    }
}
