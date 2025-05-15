package neuron.internal.signal;

import org.apache.spark.sql.Dataset;

import java.util.List;

interface IIRFilter {

    Dataset<Double> lowPassFilter(Dataset<Double> signal, double samplingRate, int order, double cutoff);

    Dataset<Double> highPassFilter(Dataset<Double> signal, double samplingRate, int order, double cutoff);

    Dataset<Double> bandPassFilter(Dataset<Double> signal, double samplingRate, double lowCut, double highCut, int order);

    Dataset<Double> bandStopFilter(Dataset<Double> signal, double samplingRate, double lowCut, double highCut, int order);
}
