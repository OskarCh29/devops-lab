package pl.devlab.traffic;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.devlab.config.ResponseDistributionConfiguration;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TrafficService {

    private final ResponseDistributionConfiguration responseConfig;

    private static final int CUMULATIVE_PERCENTAGE = 100;
    private static final HttpStatus[] HTTP_STATUSES = HttpStatus.values();

    public Mono<Void> getRandomResponse() {
        HttpStatus randomStatus = HTTP_STATUSES[ThreadLocalRandom.current().nextInt(HTTP_STATUSES.length)];
        throw new ResponseStatusException(randomStatus);
    }

    private Mono<Integer> getDistributedResponse() {
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
                    return entry.getKey();
                }
            }
            throw new IllegalStateException("Configured distribution must sum to 100");
        });
    }
}
