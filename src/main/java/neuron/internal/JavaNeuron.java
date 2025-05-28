package neuron.internal;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.List;
import java.util.stream.Collectors;

public class JavaNeuron {

    private static JavaNeuron javaNeuron;
    private SparkSession spark;
    private List<String> array;

    private JavaNeuron(SparkSession spark) {
        this.spark = spark;
    }

    public static synchronized JavaNeuron getInstance(SparkSession spark) {
        if (javaNeuron == null) {
            javaNeuron = new JavaNeuron(spark);
        }
        return javaNeuron;


    }

    public static class Util {
        private static Util util;

        private Util() {
            
        }

        public static synchronized Util getInstance() {
            if (util == null) {
                util = new Util();
            }
            return util;
        }
    }
}
