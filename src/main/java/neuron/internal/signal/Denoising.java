package neuron.internal.signal;

import org.apache.spark.sql.Dataset;

interface Denoising {
    Dataset<Double> waveletTransform(Dataset<Double> signal, int windowSize);
}
