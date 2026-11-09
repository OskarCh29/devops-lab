package pl.devlab.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseDistributionConfigurationTest {

    @Test
    @DisplayName("Accepts valid HTTP status and percentage boundaries and makes maps immutable")
    void acceptsValidConfigurationAndMakesMapsImmutable() {
        var distribution = Map.of(100, 0, 599, 100);
        var configuration = new ResponseDistributionConfiguration("normal", profiles(distribution));

        assertEquals(distribution, configuration.profiles().get("normal").distribution());
        assertThrows(
                UnsupportedOperationException.class,
                () -> configuration.profiles().clear());
        assertThrows(
                UnsupportedOperationException.class,
                () -> configuration.profiles().get("normal").distribution().clear());
    }

    @Test
    @DisplayName("Rejects missing or empty profiles")
    void rejectsMissingOrEmptyProfiles() {
        assertThrows(IllegalArgumentException.class, () -> validateConfig("normal", null));
        assertThrows(IllegalArgumentException.class, () -> validateConfig("normal", Map.of()));
    }

    @Test
    @DisplayName("Rejects a missing or unknown active profile")
    void rejectsMissingOrUnknownActiveProfile() {
        var distribution = Map.of(200, 100);

        assertThrows(IllegalArgumentException.class, () -> validateConfig(null, distribution));
        assertThrows(IllegalArgumentException.class, () -> validateConfig("missing", distribution));
    }

    @Test
    @DisplayName("Rejects missing or empty distributions")
    void rejectsMissingOrEmptyDistribution() {
        assertThrows(IllegalArgumentException.class, () -> validateConfig("normal", null));
        assertThrows(IllegalArgumentException.class, () -> validateConfig("normal", Map.of()));
    }

    @Test
    @DisplayName("Rejects invalid profile names and null profile values")
    void rejectsInvalidProfileEntries() {
        var blankName = new HashMap<String, ResponseDistributionConfiguration.Profile>();
        blankName.put(" ", new ResponseDistributionConfiguration.Profile(Map.of(200, 100)));

        var nullName = new HashMap<String, ResponseDistributionConfiguration.Profile>();
        nullName.put(null, new ResponseDistributionConfiguration.Profile(Map.of(200, 100)));

        var nullProfile = new HashMap<String, ResponseDistributionConfiguration.Profile>();
        nullProfile.put("normal", null);

        assertThrows(IllegalArgumentException.class, () -> new ResponseDistributionConfiguration(" ", blankName));
        assertThrows(IllegalArgumentException.class, () -> new ResponseDistributionConfiguration("normal", nullName));
        assertThrows(
                IllegalArgumentException.class, () -> new ResponseDistributionConfiguration("normal", nullProfile));
    }

    @Test
    @DisplayName("Rejects invalid status codes and percentages")
    void rejectsInvalidDistributionEntries() {
        assertInvalidDistribution(entry(null, 100));
        assertInvalidDistribution(Map.of(99, 100));
        assertInvalidDistribution(Map.of(600, 100));
        assertInvalidDistribution(entry(200, null));
        assertInvalidDistribution(Map.of(200, -1));
        assertInvalidDistribution(Map.of(200, 101));
    }

    @Test
    @DisplayName("Rejects distributions that do not sum to 100")
    void rejectsDistributionWithInvalidTotal() {
        assertInvalidDistribution(Map.of(200, 60));
        assertInvalidDistribution(Map.of(200, 60, 201, 50));
    }

    private static void validateConfig(String profileName, Map<Integer, Integer> distribution) {
        new ResponseDistributionConfiguration(profileName, profiles(distribution));
    }

    private static Map<String, ResponseDistributionConfiguration.Profile> profiles(Map<Integer, Integer> distribution) {
        return Map.of("normal", new ResponseDistributionConfiguration.Profile(distribution));
    }

    private static Map<Integer, Integer> entry(Integer status, Integer percentage) {
        var distribution = new HashMap<Integer, Integer>();
        distribution.put(status, percentage);
        return distribution;
    }

    private static void assertInvalidDistribution(Map<Integer, Integer> distribution) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ResponseDistributionConfiguration("normal", profiles(distribution)));
    }
}
