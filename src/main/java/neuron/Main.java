package neuron;

import neuron.internal.JavaNeuron;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        SparkSession spark = SparkSession
                .builder()
                .appName("Java Neuron")
                .master("local[*]")
                .config("spark.sql.warehouse.dir", "file:///c:tmp/")
                .getOrCreate();

        Dataset<Row> df = spark.read().option("header", true).csv("src/main/resources/06995_AF.csv");

//        df.show();

        List<String> ecgValues = df.select("'Time'")
                        .collectAsList()
                        .stream()
                        .map(row -> {
                            String val = row.getString(0);
                            return val;
//                            try {
//                                return val == null ? null : Double.parseDouble(val);
//                            } catch (NumberFormatException e) {
//                                return null;
//                            }
                        })
                        .collect(Collectors.toList());
        ecgValues.forEach(System.out::println);

        spark.close();

    }
}