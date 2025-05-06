package neuron.internal.util;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.text.ParseException;
import java.util.List;

public class Util {
    private static Util util;
    private Converter converter;
    private Calculate calculate;

    private Util() {
        converter = new Converter();
        calculate = new Calculate();
    }

    public static synchronized Util getInstance() {
        if (util == null) {
            util = new Util();
        }
        return util;
    }

    public List<Double> dfToListDouble(Dataset<Row> df, String columnName) {
        return converter.dfToListDouble(df, columnName);
    }

    public List<String> dfToListString(Dataset<Row> df, String columnName) {
        return converter.dfToListString(df, columnName);
    }

    public double calculateSamplingRate(String dateFormat, List<String> timeStamps) {
        try {
            return calculate.calculateSamplingRate(dateFormat, timeStamps);
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing timestamp: ", e);
        }
    }

}
