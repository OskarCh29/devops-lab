package pl.devlab.traffic;

import pl.devlab.traffic.common.TrafficState;

public record UpdateTrafficStateRequest(TrafficState state, Integer customRequestsPerSecond) {}
