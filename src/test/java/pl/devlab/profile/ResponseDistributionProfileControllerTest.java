package pl.devlab.profile;

import static org.mockito.BDDMockito.given;
import static pl.devlab.util.TestUtil.ACTIVE_PROFILE_RESPONSE;
import static pl.devlab.util.TestUtil.OVERLOADED_PROFILE_RESPONSE;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.devlab.util.JsonUtil;
import reactor.core.publisher.Mono;

@WebFluxTest(ResponseDistributionProfileController.class)
class ResponseDistributionProfileControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ResponseDistributionProfileService profileService;

    @Test
    @DisplayName("Returns configured inactive profile")
    void getDistributionDetailsReturnsInactiveProfile() throws IOException {
        given(profileService.getDistributionDetails("overloaded")).willReturn(Mono.just(OVERLOADED_PROFILE_RESPONSE));

        String expectedResponse = JsonUtil.getJsonFromFile("response/profile_distribution_200_response.json");

        webTestClient
                .get()
                .uri("/profiles/{profile}/distribution", "overloaded")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(expectedResponse, JsonCompareMode.STRICT);
    }

    @Test
    @DisplayName("Rejects blank profile name")
    void getDistributionDetailsRejectsBlankName() throws IOException {
        String mockResponse = JsonUtil.getJsonFromFile("response/profile_distribution_400_blank_profile_response.json");
        given(profileService.getDistributionDetails("   "))
                .willReturn(Mono.error(new InvalidProfileException("Profile name must not be blank")));

        webTestClient
                .get()
                .uri("/profiles/{profile}/distribution", "   ")
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .json(mockResponse, JsonCompareMode.LENIENT);
    }

    @Test
    @DisplayName("Rejects unknown profile")
    void getDistributionDetailsRejectsUnknownProfile() throws IOException {
        given(profileService.getDistributionDetails("test"))
                .willReturn(Mono.error(new InvalidProfileException("Profile is not configured: test")));

        String expectedResponse = JsonUtil.getJsonFromFile("response/profile_distribution_400_response.json");

        webTestClient
                .get()
                .uri("/profiles/{profile}/distribution", "test")
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .json(expectedResponse, JsonCompareMode.LENIENT);
    }

    @Test
    @DisplayName("Returns active profile")
    void getActiveDistributionReturnsActiveProfile() throws IOException {
        given(profileService.getActiveDistribution()).willReturn(Mono.just(ACTIVE_PROFILE_RESPONSE));

        String expectedResponse = JsonUtil.getJsonFromFile("response/profile_distribution_active_200_response.json");

        webTestClient
                .get()
                .uri("/profiles/active")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(expectedResponse, JsonCompareMode.STRICT);
    }
}
