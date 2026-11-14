package pl.devlab.traffic;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    public Mono<Void> getResponseBasedOnDistribution() {
        return trafficService.getDistributedResponse();
    }

    @GetMapping("/random")
    public Mono<Void> getRandomResponse() {
        return trafficService.getRandomResponse();
    }

    @GetMapping("/state")
    public Mono<TrafficStatusResponse> getTrafficStatus() {
        return trafficService.getTrafficStatus();
    }

    @PatchMapping("/state")
    public Mono<TrafficStatusResponse> adjustState(@RequestBody @Valid UpdateTrafficStateRequest request) {
        return Mono.empty();
    }

    @PostMapping("/stop")
    public Mono<?> stopTraffic() {
        return Mono.empty();
    }
}
