package pl.devlab.profile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ResponseDistributionIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("Returns the configured active profile through the HTTP API")
    void getActiveDistributionReturnsConfiguredActiveProfile() {
        webTestClient
                .get()
                .uri("/profiles/active")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.profile")
                .isEqualTo("normal")
                .jsonPath("$.active")
                .isEqualTo(true)
                .jsonPath("$.distribution.200")
                .isEqualTo(50);
    }

    @Test
    @DisplayName("Returns the configured distribution by profile name")
    void getDistributionByProfileName() {
        webTestClient
                .get()
                .uri("/profiles/{profile}/distribution", "overloaded")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.profile")
                .isEqualTo("overloaded")
                .jsonPath("$.active")
                .isEqualTo(false)
                .jsonPath("$.distribution.200")
                .isEqualTo(10)
                .jsonPath("$.distribution.500")
                .isEqualTo(70);
    }

    @Test
    @DisplayName("Should return error response when profile not exists")
    void getDistributionByProfileNameReturnsError() {
        webTestClient
                .get()
                .uri("/profiles/{profile}/distribution", "test")
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.status")
                .isEqualTo(400)
                .jsonPath("$.code")
                .isEqualTo("BAD_REQUEST")
                .jsonPath("$.message")
                .isEqualTo("Profile is not configured: test")
                .jsonPath("$.path")
                .isEqualTo("/profiles/test/distribution");
    }
}
