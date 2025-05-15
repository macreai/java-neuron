package neuron.internal.signal;

import jwave.Transform;
import jwave.transforms.AncientEgyptianDecomposition;
import jwave.transforms.FastWaveletTransform;
import jwave.transforms.WaveletPacketTransform;
import jwave.transforms.wavelets.daubechies.Daubechies4;
import jwave.transforms.wavelets.haar.Haar1;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.api.java.function.MapPartitionsFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;

import java.util.ArrayList;
import java.util.List;

public class WaveletDenoising implements Denoising {

    @Override
    public Dataset<Double> waveletTransform(Dataset<Double> signal, int windowSize) {

        Dataset<double[]> windowed = signal.mapPartitions(
                (MapPartitionsFunction<Double, double[]>) it -> {
                    List<double[]> windows = new ArrayList<>();
                    double[] buffer = new double[windowSize];
                    int index = 0;

                    while (it.hasNext()) {
                        buffer[index++] = it.next();
                        if (index == windowSize) {
                            windows.add(buffer.clone()); // clone to keep buffer reusable
                            index = 0;
                        }
                    }
                    return windows.iterator();
                }, Encoders.kryo(double[].class)
        );

        Dataset<double[]> transformed = windowed.mapPartitions(
                (MapPartitionsFunction<double[], double[]>) it -> {
                    Transform transform = new Transform(
                            new AncientEgyptianDecomposition(
                                    new WaveletPacketTransform(
                                            new Haar1( ) ) ) );
                    List<double[]> result = new ArrayList<>();
                    while (it.hasNext()) {
                        double[] input = it.next();
                        double[] output = transform.forward(input);
                        result.add(output);
                    }
                    return result.iterator();
                }, Encoders.kryo(double[].class)
        );

        Dataset<Double> flattened = transformed.flatMap(
                (FlatMapFunction<double[], Double>) array -> {
                    List<Double> result = new ArrayList<>(array.length);
                    for (double val : array) {
                        double rounded = Math.round(val * 100.0) / 100.0;
                        result.add(rounded);
                    }
                    return result.iterator();
                }, Encoders.DOUBLE()
        );

        return flattened;
    }

}
