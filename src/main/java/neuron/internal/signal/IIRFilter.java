package neuron.internal.signal;

import org.apache.spark.sql.Dataset;

import java.util.List;

interface IIRFilter {

    List<Double> bandPassFilter(List<Double> signal, double samplingRate, double lowCut, double highCut, int order);

}
