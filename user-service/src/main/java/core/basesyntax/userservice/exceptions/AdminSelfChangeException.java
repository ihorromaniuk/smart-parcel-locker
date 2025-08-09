package core.basesyntax.userservice.exceptions;

public class AdminSelfChangeException extends RuntimeException {
    public AdminSelfChangeException(String message) {
        super(message);
    }
}
