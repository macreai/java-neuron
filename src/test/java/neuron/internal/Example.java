package neuron.internal;

import neuron.internal.pojo.Stats;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

public class Example {

    @Test
    void testStats() {
        List<Stats> stats = ECGProcessor.processCSV(
                "src/main/resources/record-5m-filteredHW.csv",
                0.5,
                40.0,
                2,
                Duration.ofSeconds(10)
        );

        for (Stats stat : stats) {
            System.out.println(stat);
        }
    }

}
