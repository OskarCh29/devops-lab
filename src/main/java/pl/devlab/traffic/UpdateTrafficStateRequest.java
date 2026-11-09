package pl.devlab.traffic;

public record UpdateTrafficStateRequest(TrafficState state, Integer customRequestsPerSecond) {}
