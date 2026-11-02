package pl.devlab.profile;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.devlab.config.ResponseDistributionConfiguration;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ResponseDistributionProfileService {

    private final ResponseDistributionConfiguration config;

    public Mono<ResponseDistributionResponse> getActiveDistribution() {
        return Mono.fromCallable(() -> {
            final String activeProfile = config.activeProfile();

            return createResponse(activeProfile, true);
        });
    }

    public Mono<ResponseDistributionResponse> getDistributionDetails(String profileName) {
        return Mono.fromCallable(() -> {
            final boolean active = profileName.equals(config.activeProfile());

            return createResponse(profileName, active);
        });
    }

    private ResponseDistributionResponse createResponse(String profileName, boolean active) {
        final Map<Integer, Integer> distribution = getResponseDistributionByProfile(profileName);

        return new ResponseDistributionResponse(profileName, active, distribution);
    }

    private Map<Integer, Integer> getResponseDistributionByProfile(String profileName) {
        if (profileName == null || profileName.isBlank()) {
            throw new InvalidProfileException("Profile name must not be blank");
        }

        final var profileConfiguration = config.profiles().get(profileName);

        if (profileConfiguration == null) {
            throw new InvalidProfileException("Profile is not configured: " + profileName);
        }

        return profileConfiguration.distribution();
    }
}
