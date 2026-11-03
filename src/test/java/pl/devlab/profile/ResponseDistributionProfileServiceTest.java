package pl.devlab.profile;

import static pl.devlab.util.TestUtil.ACTIVE_PROFILE_RESPONSE;
import static pl.devlab.util.TestUtil.OVERLOADED_PROFILE_RESPONSE;
import static pl.devlab.util.TestUtil.RESPONSE_DISTRIBUTION_CONFIGURATION;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class ResponseDistributionProfileServiceTest {

    private final ResponseDistributionProfileService underTest =
            new ResponseDistributionProfileService(RESPONSE_DISTRIBUTION_CONFIGURATION);

    @Test
    @DisplayName("Returns active profile")
    void getActiveDistributionReturnsActiveProfile() {
        StepVerifier.create(underTest.getActiveDistribution())
                .expectNext(ACTIVE_PROFILE_RESPONSE)
                .verifyComplete();
    }

    @Test
    @DisplayName("Returns configured inactive profile")
    void getDistributionDetailsReturnsInactiveProfile() {
        StepVerifier.create(underTest.getDistributionDetails("overloaded"))
                .expectNext(OVERLOADED_PROFILE_RESPONSE)
                .verifyComplete();
    }

    @Test
    @DisplayName("Rejects unknown profile")
    void getDistributionDetailsRejectsUnknownProfile() {
        StepVerifier.create(underTest.getDistributionDetails("missing"))
                .expectErrorMatches(error -> error instanceof InvalidProfileException
                        && "Profile is not configured: missing".equals(error.getMessage()))
                .verify();
    }

    @Test
    @DisplayName("Rejects blank profile name")
    void getDistributionDetailsRejectsBlankName() {
        StepVerifier.create(underTest.getDistributionDetails("  "))
                .expectErrorMatches(error -> error instanceof InvalidProfileException
                        && "Profile name must not be blank".equals(error.getMessage()))
                .verify();
    }

    @Test
    @DisplayName("Rejects null profile name")
    void getDistributionDetailsRejectsNullName() {
        StepVerifier.create(underTest.getDistributionDetails(null))
                .expectErrorMatches(error -> error instanceof InvalidProfileException
                        && "Profile name must not be blank".equals(error.getMessage()))
                .verify();
    }
}
