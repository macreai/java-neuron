package neuron.internal.util;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.List;
import java.util.stream.Collectors;

class Converter {
    public List<Double> dfToListDouble(Dataset<Row> df, String columnName) {
        return df.select(columnName)
                .collectAsList()
                .stream()
                .map(row -> {
                    String value = row.getString(0);
                    try {
                        return value == null ? null : Double.parseDouble(value);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .collect(Collectors.toList());


    }

    public List<String> dfToListString(Dataset<Row> df, String columnName) {
        return df.select(columnName)
                .collectAsList()
                .stream()
                .map(row -> {
                    String value = row.getString(0);
                    return value;
                })
                .collect(Collectors.toList());


    }
}
