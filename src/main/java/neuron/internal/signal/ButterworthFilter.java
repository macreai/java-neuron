package neuron.internal.signal;

import org.apache.spark.api.java.function.MapPartitionsFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import uk.me.berndporr.iirj.Butterworth;

import java.util.ArrayList;
import java.util.List;

public class ButterworthFilter implements IIRFilter {

    @Override
    public Dataset<Double> lowPassFilter(Dataset<Double> signal, double samplingRate, int order, double cutoff) {
        return signal.mapPartitions(
                (MapPartitionsFunction<Double, Double>) iterator -> {
                    Butterworth butterworth = new Butterworth();
                    butterworth.lowPass(order, samplingRate, cutoff);
                    List<Double> result = new ArrayList<>();
                    iterator.forEachRemaining(value -> result.add(butterworth.filter(value)));
                    return result.iterator();
                },
                Encoders.DOUBLE()
        );
    }

    @Override
    public Dataset<Double> highPassFilter(Dataset<Double> signal, double samplingRate, int order, double cutoff) {
        return signal.mapPartitions(
                (MapPartitionsFunction<Double, Double>) iterator -> {
                    Butterworth butterworth = new Butterworth();
                    butterworth.highPass(order, samplingRate, cutoff);
                    List<Double> result = new ArrayList<>();
                    iterator.forEachRemaining(value -> result.add(butterworth.filter(value)));
                    return result.iterator();
                },
                Encoders.DOUBLE()
        );
    }

    @Override
    public Dataset<Double> bandPassFilter(Dataset<Double> signal, double samplingRate, float lowCut, float highCut, int order) {
        if (lowCut >= highCut) {
            throw new IllegalArgumentException("Low cutoff frequency must be less than high cutoff frequency.");
        }

        double centerFreq = (highCut + lowCut) / 2.0;
        double bandwidth = highCut - lowCut;

        return signal.mapPartitions(
                (MapPartitionsFunction<Double, Double>) iterator -> {
                    Butterworth butterworth = new Butterworth();
                    butterworth.bandPass(order, samplingRate, centerFreq, bandwidth);
                    List<Double> result = new ArrayList<>();
                    iterator.forEachRemaining(value -> result.add(butterworth.filter(value)));
                    return result.iterator();
                },
                Encoders.DOUBLE()
        );
    }

    @Override
    public Dataset<Double> bandStopFilter(Dataset<Double> signal, double samplingRate, float lowCut, float highCut, int order) {
        if (lowCut >= highCut) {
            throw new IllegalArgumentException("Low cutoff frequency must be less than high cutoff frequency.");
        }

        double centerFreq = (highCut + lowCut) / 2.0;
        double bandwidth = highCut - lowCut;

        return signal.mapPartitions(
                (MapPartitionsFunction<Double, Double>) iterator -> {
                    Butterworth butterworth = new Butterworth();
                    butterworth.bandStop(order, samplingRate, centerFreq, bandwidth);
                    List<Double> result = new ArrayList<>();
                    iterator.forEachRemaining(value -> result.add(butterworth.filter(value)));
                    return result.iterator();
                },
                Encoders.DOUBLE()
        );
    }
}
