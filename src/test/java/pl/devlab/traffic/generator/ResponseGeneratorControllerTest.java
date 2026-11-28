package pl.devlab.traffic.generator;

import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(ResponseGeneratorController.class)
class ResponseGeneratorControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ResponseGeneratorService responseGeneratorService;

    @Test
    @DisplayName("Maps status, headers and body of a generated response")
    void mapsGeneratedResponse() {
        given(responseGeneratorService.getDistributedResponse())
                .willReturn(Mono.just(GeneratedResponse.withJsonBody(
                        HttpStatus.TOO_MANY_REQUESTS, headerWith(HttpHeaders.RETRY_AFTER, "5"))));

        webTestClient
                .get()
                .uri("/generator/responses/distributed")
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.TOO_MANY_REQUESTS)
                .expectHeader()
                .valueEquals(HttpHeaders.RETRY_AFTER, "5")
                .expectBody()
                .jsonPath("$.code")
                .isEqualTo("TOO_MANY_REQUESTS");
    }

    @Test
    @DisplayName("Returns empty body for a response without body")
    void returnsEmptyBody() {
        given(responseGeneratorService.getRandomResponse())
                .willReturn(Mono.just(GeneratedResponse.withoutBody(HttpStatus.NO_CONTENT)));

        webTestClient
                .get()
                .uri("/generator/responses/random")
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody()
                .isEmpty();
    }

    @Test
    @DisplayName("Redirect target is reachable")
    void redirectTargetIsReachable() {
        webTestClient
                .get()
                .uri("/generator/responses/redirect-target")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Redirect target reached");
    }

    private static HttpHeaders headerWith(String name, String value) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(name, value);
        return headers;
    }
}
