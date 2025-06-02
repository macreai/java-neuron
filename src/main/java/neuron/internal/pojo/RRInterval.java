package neuron.internal.pojo;

import java.time.Instant;

public class RRInterval {
    private Instant timestamp;
    private double rrIntervalMs;

    public RRInterval(Instant timestamp, double rrIntervalMs) {
        this.timestamp = timestamp;
        this.rrIntervalMs = rrIntervalMs;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public double getRrIntervalMs() {
        return rrIntervalMs;
    }
}

