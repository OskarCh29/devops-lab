package pl.devlab.util;

import java.util.Map;
import pl.devlab.config.ResponseDistributionConfiguration;
import pl.devlab.profile.ResponseDistributionResponse;

public final class TestUtil {

    private TestUtil() {}

    public static final ResponseDistributionResponse OVERLOADED_PROFILE_RESPONSE =
            new ResponseDistributionResponse("overloaded", false, Map.of(500, 25, 502, 25, 503, 25, 200, 25));
    public static final ResponseDistributionResponse ACTIVE_PROFILE_RESPONSE =
            new ResponseDistributionResponse("normal", true, Map.of(500, 10, 200, 70, 400, 20));

    public static final ResponseDistributionConfiguration RESPONSE_DISTRIBUTION_CONFIGURATION =
            new ResponseDistributionConfiguration(
                    "normal",
                    Map.of(
                            "normal",
                            new ResponseDistributionConfiguration.Profile(ACTIVE_PROFILE_RESPONSE.distribution()),
                            "overloaded",
                            new ResponseDistributionConfiguration.Profile(OVERLOADED_PROFILE_RESPONSE.distribution())));
}
