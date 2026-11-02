package pl.devlab.config;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "dev.responses")
public record ResponseDistributionConfiguration(String activeProfile, Map<String, Profile> profiles) {

    public record Profile(Map<Integer, Integer> distribution) {}

    private static final int ONE_HUNDRED = 100;
    private static final int ZERO = 0;
    private static final int HIGHEST_POSSIBLE_HTTP_STATUS_VALUE = 599;
    private static final int LOWEST_POSSIBLE_HTTP_STATUS_VALUE = 100;

    public ResponseDistributionConfiguration {
        if (profiles == null || profiles.isEmpty()) {
            throw new IllegalArgumentException("At least one profile must be configured");
        }
        if (activeProfile == null || !profiles.containsKey(activeProfile)) {
            throw new IllegalArgumentException("Unknown active profile: " + activeProfile);
        }

        profiles.forEach((name, profile) -> {
            if (name == null || name.isBlank() || profile == null) {
                throw new IllegalArgumentException("Invalid profile configuration");
            }
            validateDistribution(name, profile.distribution);
        });
        profiles = profiles.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        entry -> new Profile(Map.copyOf(entry.getValue().distribution()))));
    }

    private static void validateDistribution(String profileName, Map<Integer, Integer> distribution) {
        if (distribution == null || distribution.isEmpty()) {
            throw new IllegalArgumentException("Distribution cannot be empty: " + profileName);
        }

        boolean invalidEntry = distribution.entrySet().stream().anyMatch(entry -> {
            Integer status = entry.getKey();
            Integer percentage = entry.getValue();

            return status == null
                    || status < LOWEST_POSSIBLE_HTTP_STATUS_VALUE
                    || status > HIGHEST_POSSIBLE_HTTP_STATUS_VALUE
                    || percentage == null
                    || percentage < ZERO
                    || percentage > ONE_HUNDRED;
        });

        if (invalidEntry) {
            throw new IllegalArgumentException("Invalid distribution entry in profile: " + profileName);
        }

        int total = distribution.values().stream().mapToInt(Integer::intValue).sum();

        if (total != ONE_HUNDRED) {
            throw new IllegalArgumentException(
                    "Profile %s must sum to 100%% , but was %d%%".formatted(profileName, total));
        }
    }
}
