package pl.devlab.exception;

import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import pl.devlab.common.ErrorResponse;
import pl.devlab.profile.InvalidProfileException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
            ResponseStatusException e, ServerWebExchange exchange) {
        HttpStatus status = HttpStatus.valueOf(e.getStatusCode().value());
        return createResponse(status, status.getReasonPhrase(), exchange);
    }

    @ExceptionHandler(InvalidProfileException.class)
    public ResponseEntity<ErrorResponse> handleProfileNotFoundException(
            InvalidProfileException exception, ServerWebExchange exchange) {
        return createResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), exchange);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception, ServerWebExchange exchange) {
        return createResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), exchange);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e, ServerWebExchange exchange) {
        return createResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), exchange);
    }

    private ResponseEntity<ErrorResponse> createResponse(
            HttpStatus httpStatus, String message, ServerWebExchange exchange) {
        ErrorResponse errorResponse = ErrorResponse.of(httpStatus, message, exchange, Instant.now());

        return ResponseEntity.status(httpStatus).body(errorResponse);
    }
}
