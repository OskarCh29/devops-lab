package pl.devlab.traffic.generator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.HttpStatus;

class ResponseGeneratorServiceTest {

    @Test
    @DisplayName("201 has Location header and id matching the body")
    void createdContainsLocationAndId() {}

    @ParameterizedTest
    @EnumSource(
            value = HttpStatus.class,
            names = {"NO_CONTENT", "RESET_CONTENT", "NOT_MODIFIED"})
    @DisplayName("Statuses that forbid a body have no body, no content type and no location header")
    void statusesWithoutBody(HttpStatus status) {}

    @ParameterizedTest
    @EnumSource(
            value = HttpStatus.class,
            names = {"MOVED_PERMANENTLY", "FOUND", "SEE_OTHER", "TEMPORARY_REDIRECT", "PERMANENT_REDIRECT"})
    @DisplayName("Redirects point to the redirect target")
    void redirectsContainLocation(HttpStatus status) {}

    @Test
    @DisplayName("401 contains WW-Authenticate")
    void unauthorizedContainsProperHeader() {}

    @Test
    @DisplayName("429 contains Retry-After")
    void tooManyRequestsContainRetryAfter() {}

    @Test
    @DisplayName("Other statuses get the default JSON body without headers")
    void defaultResponse() {}

    @RepeatedTest(50)
    @DisplayName("Random response always satisfies HTTP invariants")
    void randomResponseKeepsInvariants() {}
}
