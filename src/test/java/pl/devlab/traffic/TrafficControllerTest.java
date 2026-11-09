package pl.devlab.traffic;

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
import reactor.core.publisher.Mono;

@WebFluxTest(TrafficController.class)
class TrafficControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private TrafficService trafficService;

    @Test
    @DisplayName("Should return random response")
    void getRandomResponse() {
        given(trafficService.getRandomResponse())
                .willReturn(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));

        webClient
                .get()
                .uri("/traffic/random")
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

        then(trafficService).should().getRandomResponse();
    }
}
