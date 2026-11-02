package pl.devlab.profile;

public class InvalidProfileException extends RuntimeException {
    public InvalidProfileException(String message) {
        super(message);
    }
}
