package neuron.internal.signal;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.apache.spark.sql.expressions.Window;
import org.apache.spark.sql.expressions.WindowSpec;
import org.apache.spark.sql.types.StructType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import scala.Tuple2;

import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

import static org.apache.spark.sql.functions.*;

public class ButterworthFilterTest {

//    private static SparkSession spark;
//
//    @BeforeAll
//    public static void setup() {
//        spark = SparkSession.builder()
//                .appName("Butterworth Filter Test")
//                .master("local[*]")
//                .config("spark.sql.warehouse.dir", "file:///c:tmp/")
//                .getOrCreate();
//    }
//
//    @AfterAll
//    public static void tearDown() {
//        if (spark != null) {
//            spark.close();
//        }
//    }
//
//    @Test
//    public void testLowPassFilterECG1() {
//        Dataset<Row> df = spark.read().option("header", true).csv("src/main/resources/06995_AF_2_hours.csv");
//
//        Dataset<Double> ecgValues = df.select(df.col("'ECG1'").cast("double"))
//                .filter(df.col("'ECG1'").isNotNull())
//                .as(Encoders.DOUBLE());
//
//        ButterworthFilter filter = new ButterworthFilter();
//        Dataset<Double> filtered = filter
//                .lowPassFilter(ecgValues, 250, 4, 4.0);
//
//        filtered.show(100);
//    }
//
//    @Test
//    public void testLowPassFilterECG1_withCSV() {
//        Dataset<Row> df = spark.read().option("header", true).csv("src/main/resources/06995_AF.csv");
//
//        Dataset<Double> ecgValues = df.select(df.col("'ECG1'").cast("double"))
//                .filter(df.col("'ECG1'").isNotNull())
//                .as(Encoders.DOUBLE());
//
//        ButterworthFilter filter = new ButterworthFilter();
//        Dataset<Double> filtered = filter
//                .lowPassFilter(ecgValues, 250, 4, 4.0);
//
//        Dataset<String> timeValues = df.select(df.col("'Time'").cast("string"))
//                .filter(df.col("'Time'").isNotNull())
//                .as(Encoders.STRING());
//
//        Dataset<Row> filteredECG1 = filtered.withColumnRenamed("value", "ECG1_Filtered");
//
//        Dataset<Row> result = timeValues
//                .withColumn("row_index", functions.monotonically_increasing_id())
//                .join(filteredECG1.withColumn("row_index", functions.monotonically_increasing_id()), "row_index")
//                .drop("row_index");
//
//        result.show(100);
//
//        String outputPath = "src/main/resources/output_ecg_filtered";
//
//        result.coalesce(1)
//                .write()
//                .mode(SaveMode.Overwrite)
//                .option("header", "true")
//                .csv(outputPath);
//    }
//
//
//
//    @Test
//    public void testHighPassFilterECG1() {
//        Dataset<Row> df = spark.read().option("header", true).csv("src/main/resources/06995_AF_2_hours.csv");
//
//        Dataset<Double> ecgValues = df.select(df.col("'ECG1'").cast("double"))
//                .filter(df.col("'ECG1'").isNotNull())
//                .as(Encoders.DOUBLE());
//
//        ButterworthFilter filter = new ButterworthFilter();
//        Dataset<Double> filtered = filter
//                .highPassFilter(ecgValues, 250, 4, 4.0);
//
//        filtered.show(100);
//    }
//
//    @Test
//    public void testHighPassFilterECG1_withCSV() {
//        Dataset<Row> df = spark.read().option("header", true).csv("src/main/resources/06995_AF.csv");
//
//        Dataset<Double> ecgValues = df.select(df.col("ECG1").cast("double"))
//                .filter(df.col("ECG1").isNotNull())
//                .as(Encoders.DOUBLE());
//
//        ButterworthFilter filter = new ButterworthFilter();
//        Dataset<Double> filtered = filter
//                .highPassFilter(ecgValues, 250, 4, 0.5);
//
//        Dataset<String> timeValues = df.select(df.col("Time").cast("string"))
//                .filter(df.col("Time").isNotNull())
//                .as(Encoders.STRING());
//
//        Dataset<Row> filteredECG1 = filtered.withColumnRenamed("value", "'ECG1_Filtered'");
//
//        Dataset<Row> result = timeValues
//                .withColumn("row_index", functions.monotonically_increasing_id())
//                .join(filteredECG1.withColumn("row_index", functions.monotonically_increasing_id()), "row_index")
//                .drop("row_index");
//
//        result.show(100);
//
//        String outputPath = "src/main/resources/output_ecg_filtered";
//
//        result.coalesce(1)
//                .write()
//                .mode(SaveMode.Overwrite)
//                .option("header", "true")
//                .csv(outputPath);
//    }
//
//    @Test
//    public void testBandPassFilterECG1() {
//        Dataset<Row> df = spark.read().option("header", true).csv("src/main/resources/06995_AF_2_hours.csv");
//
//        Dataset<Double> ecgValues = df.select(df.col("'ECG1'").cast("double"))
//                .filter(df.col("'ECG1'").isNotNull())
//                .as(Encoders.DOUBLE());
//
//        ButterworthFilter filter = new ButterworthFilter();
//        Dataset<Double> filtered = filter
//                .bandPassFilter(ecgValues, 250, 0.5, 40.0, 4);
//
//        filtered.show(100);
//    }
//
//    @Test
//    public void testBandPassFilterECG1_withCSV() {
//        Dataset<Row> df = spark.read().option("header", true).csv("src/main/resources/record-5m-filteredHW.csv");
//
//        Dataset<Double> ecgValues = df.select(df.col("ECG").cast("double"))
//                .filter(df.col("ECG").isNotNull())
//                .as(Encoders.DOUBLE());
//
//        Dataset<String> timeValues = df.select(df.col("Time").cast("string"))
//                .filter(df.col("Time").isNotNull())
//                .as(Encoders.STRING());
//
//        ButterworthFilter filter = new ButterworthFilter();
//        Dataset<Double> filtered = filter
//                .bandPassFilter(ecgValues, 250.0, 0.5, 40, 2);
//
//
//        Dataset<Row> filteredECG1 = filtered.withColumnRenamed("value", "'ECG1_Filtered'");
//
//        Dataset<Row> result = timeValues
//                .withColumn("row_index", functions.monotonically_increasing_id())
//                .join(filteredECG1.withColumn("row_index", functions.monotonically_increasing_id()), "row_index")
//                .drop("row_index");
//
//        result.show(100);
//
//        String outputPath = "src/main/resources/output_ecg_filtered";
//
//        result.coalesce(1)
//                .write()
//                .mode(SaveMode.Overwrite)
//                .option("header", "true")
//                .csv(outputPath);
//    }
//
//    @Test
//    public void testBandPassFilterECG1_withCSV_V1() {
//        Dataset<Row> df = spark.read()
//                .option("header", true)
//                .csv("src/main/resources/record-5m-filteredHW_ORI.csv");
//
////        Median Method
//
//        Dataset<Row> medianGrouped = df
//                .filter(col("Time").isNotNull().and(col("ECG").isNotNull()))
//                .groupBy(col("Time"))
//                .agg(expr("percentile_approx(ECG, 0.5)").alias("MedianECG"))
//                .orderBy("Time");
//
//        Dataset<String> timeValues = medianGrouped.select("Time").as(Encoders.STRING());
//        Dataset<Double> ecgValues = medianGrouped.select("MedianECG").as(Encoders.DOUBLE());
//
////        Mean Method
//
////        Dataset<Row> meanGrouped = df
////                .filter(col("Time").isNotNull().and(col("ECG").isNotNull()))
////                .groupBy(col("Time"))
////                .agg(avg("ECG").alias("MeanECG"))
////                .orderBy("Time");
//
////        Dataset<String> timeValues = meanGrouped.select("Time").as(Encoders.STRING());
////        Dataset<Double> ecgValues = meanGrouped.select("MeanECG").as(Encoders.DOUBLE());
//
//        double samplingRate = calculateSamplingRateFromTimes(timeValues);
//
//        ButterworthFilter filter = new ButterworthFilter();
//        Dataset<Double> filtered = filter.bandPassFilter(ecgValues, samplingRate, 0.5, 40, 2);
//
//        Dataset<Row> filteredECG1 = filtered.withColumnRenamed("value", "ECG1_Filtered");
//
//        Dataset<Row> result = timeValues
//                .withColumn("row_index", monotonically_increasing_id())
//                .join(filteredECG1.withColumn("row_index", monotonically_increasing_id()), "row_index")
//                .drop("row_index");
//
////        result.show(100);
//
////        String outputPath = "src/main/resources/output_ecg_filtered";
////        result.coalesce(1)
////                .write()
////                .mode(SaveMode.Overwrite)
////                .option("header", "true")
////                .csv(outputPath);
//
//        double maxVal = filtered.agg(max("value")).first().getDouble(0);
//        double threshold = maxVal * 0.6;
//        Dataset<Row> resultWithRPeaks = markRPeaks(result, threshold);
//
//        System.out.println("Final Result with R-Peaks:");
//        resultWithRPeaks.show(10, false);
//
//        String outputPath = "src/main/resources/output_ecg_filtered";
//        resultWithRPeaks.coalesce(1)
//                .write()
//                .mode(SaveMode.Overwrite)
//                .option("header", "true")
//                .csv(outputPath);
//
//        Dataset<Row> rrIntervals = calculateRRIntervals(resultWithRPeaks);
//
//        rrIntervals.show(10, false);
//
//        rrIntervals.coalesce(1)
//                .write()
//                .mode(SaveMode.Overwrite)
//                .option("header", "true")
//                .csv("src/main/resources/output_rr_intervals");
//
//        Dataset<Row> rrStats10s = calculateRRStatisticsInWindow(rrIntervals, "10 seconds");
//        rrStats10s.show(10,false);
//
//        rrStats10s.coalesce(1)
//                .write()
//                .mode(SaveMode.Overwrite)
//                .option("header", "true")
//                .csv("src/main/resources/output_rr_stats_10s");
//
//    }
//
//
//    public double calculateSamplingRateFromTimes(Dataset<String> timeValues) {
//        List<String> times = timeValues.takeAsList(2);
//
//        if (times.size() < 2) {
//            throw new IllegalArgumentException("Dataset harus memiliki minimal dua timestamp berbeda.");
//        }
//
//        Instant t1 = Instant.parse(times.get(0) + "Z");
//        Instant t2 = Instant.parse(times.get(1) + "Z");
//
//        long diffMillis = Math.abs(t2.toEpochMilli() - t1.toEpochMilli());
//        double diffSeconds = diffMillis / 1000.0;
//
//        if (diffSeconds == 0.0) {
//            throw new ArithmeticException("Timestamp identik — sampling rate tidak bisa dihitung.");
//        }
//
//        return 1.0 / diffSeconds;
//    }
//
//    public Dataset<Row> markRPeaks(Dataset<Row> dataset, double threshold) {
//        WindowSpec window = Window.orderBy("Time");
//
//        Dataset<Row> ecgWithNeighbors = dataset
//                .withColumn("prev", lag("ECG1_Filtered", 1).over(window))
//                .withColumn("next", lead("ECG1_Filtered", 1).over(window));
//
//        Dataset<Row> withRPeakFlag = ecgWithNeighbors.withColumn("is_r_peak",
//                col("ECG1_Filtered").gt(threshold)
//                        .and(col("ECG1_Filtered").gt(col("prev")))
//                        .and(col("ECG1_Filtered").gt(col("next"))));
//
//        return withRPeakFlag.drop("prev", "next");
//    }
//
//    public Dataset<Row> calculateRRIntervals(Dataset<Row> dfWithRPeaks) {
//        WindowSpec orderByTime = Window.orderBy("Time");
//
//        Dataset<Row> rPeakRows = dfWithRPeaks.filter(col("is_r_peak"));
//
//        Dataset<Row> withTimestamps = rPeakRows
//                .withColumn("timestamp", to_timestamp(col("Time"), "yyyy-MM-dd'T'HH:mm:ss.SSS"));
//
//        Dataset<Row> withPrevTimestamps = withTimestamps
//                .withColumn("prev_timestamp", lag("timestamp", 1).over(orderByTime));
//
//        Dataset<Row> withRR = withPrevTimestamps.withColumn("rr_interval_ms",
//                round(col("timestamp").cast("double")
//                        .minus(col("prev_timestamp").cast("double"))
//                        .multiply(1000), 2));
//
//        return withRR.filter(col("rr_interval_ms").isNotNull());
//    }
//
//    public Dataset<Row> calculateRRStatisticsInWindow(Dataset<Row> rrIntervals, String windowDuration) {
//
//        Dataset<Row> withTimestamp = rrIntervals.withColumn(
//                "timestamp", to_timestamp(col("Time"), "yyyy-MM-dd'T'HH:mm:ss.SSS"));
//
//        Dataset<Row> stats = withTimestamp
//                .filter(col("rr_interval_ms").isNotNull())
//                .groupBy(window(col("timestamp"), windowDuration).alias("time_window"))
//                .agg(
//                        round(min("rr_interval_ms").alias("min_rr"),2),
//                        round(max("rr_interval_ms").alias("max_rr"),2),
//                        round(mean("rr_interval_ms").alias("mean_rr"),2),
//                        round(expr("percentile_approx(rr_interval_ms, 0.5)").alias("median_rr"),2),
//                        round(stddev("rr_interval_ms").alias("std_rr"),2)
//                )
//                .orderBy("time_window");
//
//        return stats
////                .withColumn("window_start", col("time_window.start"))
////                .withColumn("window_end", col("time_window.end"))
//                .drop("time_window");
//    }
}
