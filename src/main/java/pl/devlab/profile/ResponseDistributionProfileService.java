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
            String activeProfile = config.activeProfile();
            Map<Integer, Integer> distribution = getResponseDistributionByProfile(activeProfile);
            return new ResponseDistributionResponse(activeProfile, true, distribution);
        });
    }

    public Mono<ResponseDistributionResponse> getDistributionDetails(String profileName) {
        return Mono.fromCallable(() -> {
            Map<Integer, Integer> distribution = getResponseDistributionByProfile(profileName);

            return new ResponseDistributionResponse(
                    profileName, profileName.equals(config.activeProfile()), distribution);
        });
    }

    public Map<Integer, Integer> getResponseDistributionByProfile(String profileName) {
        var profileConfiguration = config.profiles().get(profileName);

        if (profileConfiguration == null) {
            throw new IllegalArgumentException("Profile is not configured: " + profileName);
        }
        return profileConfiguration.distribution();
    }
}
