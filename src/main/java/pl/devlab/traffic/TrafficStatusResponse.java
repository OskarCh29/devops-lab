package pl.devlab.traffic;

public record TrafficStatusResponse(TrafficState state, int requestsPerSecond) {

    public TrafficStatusResponse(TrafficState state) {
        this(state, state.requestsPerSecond());
    }
}
