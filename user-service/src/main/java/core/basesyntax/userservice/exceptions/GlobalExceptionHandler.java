package core.basesyntax.userservice.exceptions;

import core.basesyntax.userservice.dto.exception.ExceptionDto;
import core.basesyntax.userservice.dto.exception.ValidationExceptionDto;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
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

    @ExceptionHandler({AuthorizationDeniedException.class, AdminSelfChangeException.class})
    public ResponseEntity<ExceptionDto> handleForbiddenException(
            Exception ex) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ExceptionDto exceptionDto = new ExceptionDto(status, ex.getMessage());
        return new ResponseEntity<>(exceptionDto, status);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionDto> handleBadCredentialsException(
            BadCredentialsException ex) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ExceptionDto exceptionDto = new ExceptionDto(status, ex.getMessage());
        return new ResponseEntity<>(exceptionDto, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        Set<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(this::getErrorMessage)
                .collect(Collectors.toSet());
        ValidationExceptionDto exceptionDto = new ValidationExceptionDto(httpStatus, errors);
        return new ResponseEntity<>(exceptionDto, httpStatus);
    }

    private String getErrorMessage(ObjectError error) {
        if (error instanceof FieldError) {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            return field + " " + message;
        }
        return error.getDefaultMessage();
    }
}
