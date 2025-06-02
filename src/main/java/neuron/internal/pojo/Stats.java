package neuron.internal.pojo;

import java.time.Instant;

public class Stats {
    private final Instant windowStart;
    private final Instant windowEnd;
    private final double min;
    private final double max;
    private final double mean;
    private final double median;
    private final double std;

    public Stats(Instant windowStart, Instant windowEnd, double min, double max, double mean, double median, double std) {
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.min = min;
        this.max = max;
        this.mean = mean;
        this.median = median;
        this.std = std;
    }

    @Override
    public String toString() {
        return "RRIntervalStats{" +
                "min=" + min +
                ", max=" + max +
                ", mean=" + mean +
                ", median=" + median +
                ", std=" + std +
                '}';
    }

}
