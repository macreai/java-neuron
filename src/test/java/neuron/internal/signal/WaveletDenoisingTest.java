package neuron.internal.signal;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WaveletDenoisingTest {

    private static SparkSession spark;

    @BeforeAll
    public static void setup() {
        spark = SparkSession.builder()
                .appName("Butterworth Filter Test")
                .master("local[*]")
                .config("spark.sql.warehouse.dir", "file:///c:tmp/")
                .getOrCreate();
    }

    @AfterAll
    public static void tearDown() {
        if (spark != null) {
            spark.close();
        }
    }

    @Test
    void testWavelet() {
        Dataset<Row> df = spark.read().option("header", true).csv("src/main/resources/06995_AF_2_hours.csv");

        Dataset<Double> ecgValues = df.select(df.col("'ECG1'").cast("double"))
                .filter(df.col("'ECG1'").isNotNull())
                .as(Encoders.DOUBLE());

        ButterworthFilter filter = new ButterworthFilter();
        Dataset<Double> filtered = filter
                .highPassFilter(ecgValues, 250, 4, 0.5);

        filtered.show();

        WaveletDenoising wavelet = new WaveletDenoising();
        Dataset<Double> cleanSignal = wavelet.waveletTransform(filtered, 250);

        cleanSignal.show();
    }
}