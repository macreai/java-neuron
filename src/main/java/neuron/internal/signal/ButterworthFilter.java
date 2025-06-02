package neuron.internal.signal;

import uk.me.berndporr.iirj.Butterworth;

import java.util.ArrayList;
import java.util.List;

public class ButterworthFilter implements IIRFilter {

    public List<Double> bandPassFilter(List<Double> signal, double samplingRate, double lowCut, double highCut, int order) {
        if (lowCut >= highCut) {
            throw new IllegalArgumentException("Low cutoff frequency must be less than high cutoff frequency.");
        }

        double centerFreq = (highCut + lowCut) / 2.0;
        double bandwidth = highCut - lowCut;

        Butterworth butterworth = new Butterworth();
        butterworth.bandPass(order, samplingRate, centerFreq, bandwidth);

        List<Double> filteredSignal = new ArrayList<>();

        for (double value : signal) {
            double filtered = butterworth.filter(value);
            double rounded = Math.round(filtered * 100.0) / 100.0;
            filteredSignal.add(rounded);
        }

        return filteredSignal;
    }

}
