package neuron.internal.signal;

import org.apache.spark.sql.*;
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
        Dataset<Row> df = spark.read().option("header", true).csv("src/main/resources/06995_AF.csv");

        Dataset<Double> ecgValues = df.select(df.col("'ECG1'").cast("double"))
                .filter(df.col("'ECG1'").isNotNull())
                .as(Encoders.DOUBLE());

        ButterworthFilter filter = new ButterworthFilter();
        Dataset<Double> filtered = filter
                .highPassFilter(ecgValues, 250, 4, 0.5);

        filtered.show();

        WaveletDenoising wavelet = new WaveletDenoising();
        Dataset<Double> cleanSignal = wavelet.waveletTransform(filtered, 256);

        cleanSignal.show();
    }

    @Test
    void testWavelet_withCSV() {
        Dataset<Row> df = spark.read().option("header", true).csv("src/main/resources/06995_AF.csv");

        Dataset<Double> ecgValues = df.select(df.col("'ECG1'").cast("double"))
                .filter(df.col("'ECG1'").isNotNull())
                .as(Encoders.DOUBLE());

        ButterworthFilter filter = new ButterworthFilter();
        Dataset<Double> filtered = filter
                .highPassFilter(ecgValues, 250, 4, 0.5);

        filtered.show();

        WaveletDenoising wavelet = new WaveletDenoising();
        Dataset<Double> cleanSignal = wavelet.waveletTransform(filtered, 256);

        Dataset<String> timeValues = df.select(df.col("'Time'").cast("string"))
                .filter(df.col("'Time'").isNotNull())
                .as(Encoders.STRING());

        Dataset<Row> filteredECG1 = cleanSignal.withColumnRenamed("value", "'ECG1_Filtered'");

        Dataset<Row> result = timeValues
                .withColumn("row_index", functions.monotonically_increasing_id())
                .join(filteredECG1.withColumn("row_index", functions.monotonically_increasing_id()), "row_index")
                .drop("row_index");

        result.show(100);

        String outputPath = "src/main/resources/output_ecg_filtered";

        result.coalesce(1)
                .write()
                .mode(SaveMode.Overwrite)
                .option("header", "true")
                .csv(outputPath);

        cleanSignal.show();
    }
}