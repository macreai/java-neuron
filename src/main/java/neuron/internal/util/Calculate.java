package neuron.internal.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

class Calculate {
    public double calculateSamplingRate(String dateFormat, List<String> timeStamps) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

        List<Long> timeMillis = new ArrayList<>();
        for (String ts : timeStamps) {
            String clean = ts.replace("[", "").replace("]","");
            timeMillis.add(sdf.parse(clean).getTime());
        }

        List<Long> intervals = new ArrayList<>();
        for (int i = 1; i < timeMillis.size(); i++) {
            intervals.add(timeMillis.get(i) - timeMillis.get(i - 1));
        }

        double averageIntervalMs = intervals.stream().mapToDouble(Long::doubleValue).average().orElse(0);
        double samplingRateHz = 1000.0 / averageIntervalMs;

        return samplingRateHz;
    }
}
