package pl.devlab.common;

import java.time.Instant;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;

@Builder
public record ErrorResponse(int status, String code, String message, String path, Instant timeStamp) {

    public static ErrorResponse of(
            HttpStatus httpStatus, String message, ServerWebExchange serverWebExchange, Instant timeStamp) {
        return ErrorResponse.builder()
                .status(httpStatus.value())
                .code(httpStatus.name())
                .message(message)
                .path(serverWebExchange.getRequest().getPath().value())
                .timeStamp(timeStamp)
                .build();
    }
}
