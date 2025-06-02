package neuron.internal.pojo;

import java.time.Instant;

public class ECGSample {
    private Instant timestamp;
    private double value;

    public ECGSample(Instant timestamp, double value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public Instant getTimestamp() { return timestamp; }
    public double getValue() { return value; }
}

