package pl.devlab.traffic;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/traffic")
public class TrafficController {

    private final TrafficService trafficService;

    @GetMapping("/distributed")
    public Mono<?> getResponseBasedOnDistribution() {
        return Mono.empty();
    }

    @GetMapping("/random")
    public Mono<Void> getRandomResponse() {
        return trafficService.getRandomResponse();
    }

    @GetMapping("/state")
    public Mono<TrafficStatusResponse> getTrafficStatus() {
        return trafficService.getTrafficStatus();
    }

    @PutMapping("/state")
    public Mono<TrafficStatusResponse> adjustState(@RequestBody UpdateTrafficStateRequest request) {
        return Mono.empty();
    }

    @PostMapping("/stop")
    public Mono<?> stopTraffic() {
        return Mono.empty();
    }
}
