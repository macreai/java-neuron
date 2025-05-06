package neuron.internal.signal;

import uk.me.berndporr.iirj.Butterworth;

import java.util.ArrayList;
import java.util.List;

public class ButterworthFilter implements IIRFilter {

    @Override
    public List<Double> lowPassFilter(List<Double> signal, double samplingRate,  int order, double cutoff) {
        Butterworth butterworth = new Butterworth();
        butterworth.lowPass(order, samplingRate, cutoff);

        int signalLength = signal.size();
        List<Double> filteredSignal = new ArrayList<>(signalLength);

        for (int i = 0; i < signalLength; i++) {
            double filtered = butterworth.filter(signal.get(i));
            filteredSignal.add(filtered);
        }

        return filteredSignal;
    }

    @Override
    public List<Double> highPassFilter(List<Double> signal, double samplingRate, int order, double cutoff) {
        Butterworth butterworth = new Butterworth();
        butterworth.highPass(order, samplingRate, cutoff);

        int signalLength = signal.size();
        List<Double> filteredSignal = new ArrayList<>(signalLength);

        for (int i = 0; i < signalLength; i++) {
            double filtered = butterworth.filter(signal.get(i));
            filteredSignal.add(filtered);
        }

        return filteredSignal;
    }

    @Override
    public List<Double> bandPassFilter(List<Double> signal, double samplingRate, float lowCut, float highCut, int order) {
        if (lowCut >= highCut) {
            throw new IllegalArgumentException("Low cutoff frequency must be less than high cutoff frequency.");
        }

        double centerFreq = (highCut + lowCut) / 2.0;
        double bandwidth = highCut - lowCut;

        Butterworth butterworth = new Butterworth();
        butterworth.bandPass(order, samplingRate, centerFreq, bandwidth);

        int signalLength = signal.size();
        List<Double> filteredSignal = new ArrayList<>(signalLength);

        for (int i = 0; i < signalLength; i++) {
            double filtered = butterworth.filter(signal.get(i));
            filteredSignal.add(filtered);
        }

        return filteredSignal;
    }

    @Override
    public List<Double> bandStopFilter(List<Double> signal, double samplingRate, float lowCut, float highCut, int order) {
        if (lowCut >= highCut) {
            throw new IllegalArgumentException("Low cutoff frequency must be less than high cutoff frequency.");
        }

        double centerFreq = (highCut + lowCut) / 2.0;
        double bandwidth = highCut - lowCut;

        Butterworth butterworth = new Butterworth();
        butterworth.bandStop(order, samplingRate, centerFreq, bandwidth);

        int signalLength = signal.size();
        List<Double> filteredSignal = new ArrayList<>(signalLength);

        for (int i = 0; i < signalLength; i++) {
            double filtered = butterworth.filter(signal.get(i));
            filteredSignal.add(filtered);
        }

        return filteredSignal;
    }
}
