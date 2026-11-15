package pl.devlab.traffic.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/generator/responses")
public class ResponseGeneratorController {

    private final ResponseGeneratorService responseGeneratorService;

    @GetMapping("/random")
    public Mono<Void> getRandomResponse() {
        return responseGeneratorService.getRandomResponse();
    }

    @GetMapping("/distributed")
    public Mono<Void> getDistributedResponse() {
        return responseGeneratorService.getDistributedResponse();
    }
}
