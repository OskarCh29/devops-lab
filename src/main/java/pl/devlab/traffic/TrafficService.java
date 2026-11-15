package pl.devlab.traffic;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import pl.devlab.traffic.common.TrafficState;
import pl.devlab.traffic.common.TrafficStatusResponse;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class TrafficService {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String TRAFFIC_STATE_KEY = "traffic:state";

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
}
