package pl.devlab.traffic.generator;

import static pl.devlab.traffic.generator.GeneratedResponse.withJsonBody;
import static pl.devlab.traffic.generator.GeneratedResponse.withoutBody;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.devlab.config.ResponseDistributionConfiguration;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ResponseGeneratorService {

    private static final String RESOURCE_PATH = "/generator/resources/";
    private static final String REDIRECT_TARGET = "/generator/responses/redirect-target";
    private static final String RETRY_AFTER_SECONDS = "5";
    private static final String BEARER_REALM = "Bearer realm=\"simulator\"";
    private static final int CUMULATIVE_PERCENTAGE = 100;
    private static final HttpStatus[] HTTP_STATUSES = Arrays.stream(HttpStatus.values())
            .filter(status -> status.value() >= 200)
            .toArray(HttpStatus[]::new);

    private final ResponseDistributionConfiguration responseConfig;

    public Mono<GeneratedResponse> getRandomResponse() {
        return Mono.fromSupplier(() -> {
            HttpStatus randomStatus = HTTP_STATUSES[ThreadLocalRandom.current().nextInt(HTTP_STATUSES.length)];
            return createResponse(randomStatus);
        });
    }

    public Mono<GeneratedResponse> getDistributedResponse() {
        return Mono.fromSupplier(() -> {
            Map<Integer, Integer> distribution = responseConfig
                    .profiles()
                    .get(responseConfig.activeProfile())
                    .distribution();

            int draw = ThreadLocalRandom.current().nextInt(CUMULATIVE_PERCENTAGE);
            int sum = 0;

            for (Map.Entry<Integer, Integer> entry : distribution.entrySet()) {
                sum += entry.getValue();
                if (draw < sum) {
                    return createResponse(HttpStatus.valueOf(entry.getKey()));
                }
            }
            throw new IllegalStateException("Configured distribution must sum to 100");
        });
    }

    private GeneratedResponse createResponse(HttpStatus status) {
        return switch (status) {
            case CREATED -> created();
            case NO_CONTENT, RESET_CONTENT, NOT_MODIFIED -> withoutBody(status);
            case UNAUTHORIZED -> withJsonBody(status, header(HttpHeaders.WWW_AUTHENTICATE, BEARER_REALM));
            case TOO_MANY_REQUESTS -> withJsonBody(status, header(HttpHeaders.RETRY_AFTER, RETRY_AFTER_SECONDS));
            default ->
                status.is3xxRedirection()
                        ? withJsonBody(status, header(HttpHeaders.LOCATION, REDIRECT_TARGET))
                        : withJsonBody(status, new HttpHeaders());
        };
    }

    private GeneratedResponse created() {
        String id = UUID.randomUUID().toString();
        return GeneratedResponse.withJsonBody(
                HttpStatus.CREATED, header(HttpHeaders.LOCATION, RESOURCE_PATH + id), Map.of("id", id));
    }

    private static HttpHeaders header(String name, String value) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(name, value);
        return headers;
    }
}
