package pl.devlab.traffic.generator;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public Mono<ResponseEntity<Object>> getRandomResponse() {
        return responseGeneratorService.getRandomResponse().map(this::toResponseEntity);
    }

    @GetMapping("/distributed")
    public Mono<ResponseEntity<Object>> getDistributedResponse() {
        return responseGeneratorService.getDistributedResponse().map(this::toResponseEntity);
    }

    @GetMapping("/redirect-target")
    public Mono<Map<String, String>> redirectTarget() {
        return Mono.just(Map.of("message", "Redirect target reached"));
    }

    private ResponseEntity<Object> toResponseEntity(GeneratedResponse response) {
        ResponseEntity.BodyBuilder builder =
                ResponseEntity.status(response.status()).headers(response.headers());
        return response.body() == null ? builder.build() : builder.body(response.body());
    }
}
