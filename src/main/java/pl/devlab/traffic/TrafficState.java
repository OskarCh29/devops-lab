package pl.devlab.traffic;

public enum TrafficState {
    STOPPED(0),
    SMALL(1),
    MEDIUM(5),
    HIGH(10),
    CUSTOM(-1);

    private final int requestsPerSecond;

    TrafficState(int requestsPerSecond) {
        this.requestsPerSecond = requestsPerSecond;
    }

    public int requestsPerSecond() {
        return requestsPerSecond;
    }
}
