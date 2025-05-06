package neuron.internal.signal;

import java.util.List;

interface IIRFilter {

    List<Double> lowPassFilter(List<Double> signal, double samplingRate, int order, double cutoff);

    List<Double> highPassFilter(List<Double> signal, double samplingRate, int order, double cutoff);

    List<Double> bandPassFilter(List<Double> signal, double samplingRate, float lowCut, float highCut, int order);

    List<Double> bandStopFilter(List<Double> signal, double samplingRate, float lowCut, float highCut, int order);
}
