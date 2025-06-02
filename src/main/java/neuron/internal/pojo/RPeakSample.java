package neuron.internal.pojo;

import java.time.Instant;

public class RPeakSample extends ECGSample {
    private boolean isRPeak;

    public RPeakSample(Instant timestamp, double value, boolean isRPeak) {
        super(timestamp, value);
        this.isRPeak = isRPeak;
    }

    public boolean isRPeak() {
        return isRPeak;
    }
}

