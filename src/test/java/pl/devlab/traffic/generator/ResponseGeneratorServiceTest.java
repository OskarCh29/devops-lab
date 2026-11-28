package pl.devlab.traffic.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import pl.devlab.config.ResponseDistributionConfiguration;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class ResponseGeneratorServiceTest {

    @Test
    @DisplayName("201 has Location header and id matching the body")
    void createdContainsLocationAndId() {
        ResponseGeneratorService underTest = responseGeneratorWith(HttpStatus.CREATED);

        StepVerifier.create(underTest.getDistributedResponse())
                .assertNext(response -> {
                    assertEquals(HttpStatus.CREATED, response.status());
                    Map<?, ?> body = assertInstanceOf(Map.class, response.body());
                    Object id = body.get("id");
                    assertNotNull(id);
                    assertEquals(
                            "/generator/resources/" + id, response.headers().getFirst(HttpHeaders.LOCATION));
                })
                .verifyComplete();
    }

    @ParameterizedTest
    @EnumSource(
            value = HttpStatus.class,
            names = {"NO_CONTENT", "RESET_CONTENT", "NOT_MODIFIED"})
    @DisplayName("Statuses that forbid a body have no body and no location header")
    void statusesWithoutBody(HttpStatus status) {
        ResponseGeneratorService underTest = responseGeneratorWith(status);

        StepVerifier.create(underTest.getDistributedResponse())
                .assertNext(response -> {
                    assertEquals(response.status(), status);
                    assertNull(response.body());
                    assertTrue(response.headers().isEmpty());
                    assertNull(response.headers().getContentType());
                })
                .verifyComplete();
    }

    @ParameterizedTest
    @EnumSource(
            value = HttpStatus.class,
            names = {"MOVED_PERMANENTLY", "FOUND", "SEE_OTHER", "TEMPORARY_REDIRECT", "PERMANENT_REDIRECT"})
    @DisplayName("Redirects point to the redirect target")
    void redirectsContainLocation(HttpStatus status) {
        ResponseGeneratorService underTest = responseGeneratorWith(status);

        StepVerifier.create(underTest.getDistributedResponse())
                .assertNext(response -> {
                    assertEquals(response.status(), status);
                    assertNotNull(response.headers());
                    assertEquals(
                            "/generator/responses/redirect-target",
                            response.headers().getFirst(HttpHeaders.LOCATION));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("401 contains WW-Authenticate")
    void unauthorizedContainsProperHeader() {
        ResponseGeneratorService underTest = responseGeneratorWith(HttpStatus.UNAUTHORIZED);

        StepVerifier.create(underTest.getDistributedResponse())
                .assertNext(response -> {
                    assertEquals(
                            "Bearer realm=\"simulator\"", response.headers().getFirst(HttpHeaders.WWW_AUTHENTICATE));
                    assertNotNull(response.body());
                    assertEquals(HttpStatus.UNAUTHORIZED, response.status());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("429 contains Retry-After")
    void tooManyRequestsContainRetryAfter() {
        ResponseGeneratorService underTest = responseGeneratorWith(HttpStatus.TOO_MANY_REQUESTS);

        StepVerifier.create(underTest.getDistributedResponse())
                .assertNext(response -> {
                    assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.status());
                    assertNotNull(response.body());
                    assertEquals("5", response.headers().getFirst(HttpHeaders.RETRY_AFTER));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Other statuses get the default JSON body without headers")
    void defaultResponse() {
        ResponseGeneratorService underTest = responseGeneratorWith(HttpStatus.INTERNAL_SERVER_ERROR);

        StepVerifier.create(underTest.getDistributedResponse())
                .assertNext(response -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.status());
                    Map<?, ?> body = assertInstanceOf(Map.class, response.body());
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.get("status"));
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.name(), body.get("code"));
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), body.get("message"));
                    assertEquals(MediaType.APPLICATION_JSON, response.headers().getContentType());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Random response always satisfies HTTP invariants")
    void randomResponseKeepsInvariants() {
        ResponseGeneratorService underTest = responseGeneratorWith(HttpStatus.OK);

        StepVerifier.create(Flux.range(0, 100).concatMap(i -> underTest.getRandomResponse()))
                .thenConsumeWhile(response -> true, response -> {
                    assertTrue(response.status().value() >= HttpStatus.OK.value());
                    assertEquals(
                            response.body() != null,
                            MediaType.APPLICATION_JSON.equals(response.headers().getContentType()));
                })
                .verifyComplete();
    }

    private ResponseGeneratorService responseGeneratorWith(HttpStatus status) {
        var profile = new ResponseDistributionConfiguration.Profile(Map.of(status.value(), 100));
        var config = new ResponseDistributionConfiguration("test", Map.of("test", profile));
        return new ResponseGeneratorService(config);
    }
}
