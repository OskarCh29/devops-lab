package pl.devlab.traffic;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.devlab.config.ResponseDistributionConfiguration;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class TrafficService {

    private final ResponseDistributionConfiguration responseConfig;
    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String TRAFFIC_STATE_KEY = "traffic:state";

    private static final int CUMULATIVE_PERCENTAGE = 100;
    private static final HttpStatus[] HTTP_STATUSES = HttpStatus.values();

    public Mono<Void> getRandomResponse() {
        HttpStatus randomStatus = HTTP_STATUSES[ThreadLocalRandom.current().nextInt(HTTP_STATUSES.length)];
        throw new ResponseStatusException(randomStatus);
    }

    public Mono<TrafficStatusResponse> getTrafficStatus() {
        return redisTemplate
                .opsForValue()
                .get(TRAFFIC_STATE_KEY)
                .map(this::deserializeTrafficState)
                .defaultIfEmpty(new TrafficStatusResponse(TrafficState.STOPPED));
    }

    private TrafficStatusResponse deserializeTrafficState(String value) {
        return objectMapper.readValue(value, TrafficStatusResponse.class);
    }

    private String serializeTrafficState(TrafficStatusResponse response) {
        return objectMapper.writeValueAsString(response);
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
