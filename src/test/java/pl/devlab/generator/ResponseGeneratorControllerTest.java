package pl.devlab.generator;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import pl.devlab.traffic.generator.ResponseGeneratorController;
import pl.devlab.traffic.generator.ResponseGeneratorService;
import reactor.core.publisher.Mono;

@WebFluxTest(ResponseGeneratorController.class)
class ResponseGeneratorControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private ResponseGeneratorService responseGeneratorService;

    @Test
    @DisplayName("Should return distributed response")
    void getDistributedResponse() {
        given(responseGeneratorService.getDistributedResponse())
                .willReturn(Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)));

        webClient
                .get()
                .uri("/generator/responses/distributed")
                .exchange()
                .expectStatus()
                .is5xxServerError()
                .expectBody()
                .jsonPath("$.status")
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .jsonPath("$.code")
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .jsonPath("$.message")
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

        then(responseGeneratorService).should().getDistributedResponse();
    }

    @Test
    @DisplayName("Should return random response")
    void getRandomResponse() {
        given(responseGeneratorService.getRandomResponse())
                .willReturn(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));

        webClient
                .get()
                .uri("/generator/responses/random")
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.status")
                .isEqualTo(HttpStatus.NOT_FOUND.value())
                .jsonPath("$.code")
                .isEqualTo(HttpStatus.NOT_FOUND.name())
                .jsonPath("$.message")
                .isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());

        then(responseGeneratorService).should().getRandomResponse();
    }
}
