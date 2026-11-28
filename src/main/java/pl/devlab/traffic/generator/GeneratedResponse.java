package pl.devlab.traffic.generator;

import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public record GeneratedResponse(HttpStatus status, HttpHeaders headers, Object body) {

    public static GeneratedResponse withoutBody(HttpStatus status) {
        return new GeneratedResponse(status, new HttpHeaders(), null);
    }

    public static GeneratedResponse withJsonBody(HttpStatus status, HttpHeaders headers, Object body) {
        HttpHeaders copy = new HttpHeaders();
        copy.putAll(headers);
        copy.setContentType(MediaType.APPLICATION_JSON);
        return new GeneratedResponse(status, copy, body);
    }

    public static GeneratedResponse withJsonBody(HttpStatus status, HttpHeaders headers) {
        return withJsonBody(
                status,
                headers,
                Map.of(
                        "status", status.value(),
                        "code", status.name(),
                        "message", status.getReasonPhrase()));
    }
}
